package com.virtual.cloud.om.agent.service.logs;

import com.virtual.cloud.om.agent.entity.LogMetaData;
import com.virtual.cloud.om.agent.entity.RealTimeLogStrategy;
import com.virtual.cloud.om.agent.service.kafka.KafkaConsumers;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.sdk.config.elasticsearch.EsOperation;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsole;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.*;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.FuncUtil;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: w22798
 * @Date: 2022/5/4 14:51
 */
@Slf4j
@Service("realTimeLogApi")
public class RealTimeLogService implements RealTimeLogApi, ApplicationRunner {

    @Resource
    private CasRestConnection casRestConnection;

    @Resource
    private HostApi[] hostApis;

    @Resource
    private MongoTemplate mongoTemplate;

    private DeployApi deployApi;

    @Resource
    private KafkaConsole kafkaConsole;


    private ResourceApi resourceApi;

    @Resource
    private DataCenterApi dataCenterApi;

    @Resource
    private LockApi lockApi;

    @Resource
    private LogPatternApi[] logPatternApis;

    @Resource
    private KafkaConsumers kafkaConsumers;

    @Resource
    private EsOperation esOperation;

    @Autowired
    public void setResourceApi(ResourceApi resourceApi) {
        this.resourceApi = resourceApi;
    }

    private Map<ReportResourceEnum, HostApi> hostApiMap = new ConcurrentHashMap();
    private Map<String, LogPatternApi> logPatternApiMap = new ConcurrentHashMap();

    @Autowired
    public void setDeployApi(DeployApi deployApi) {
        this.deployApi = deployApi;
    }

    public Map<String, String> logPathTargetType = new ConcurrentHashMap<>();
    public Map<String, String> logPathLogType = new ConcurrentHashMap<>();

    private Optional<RealTimeLogStrategy> convert(RealTimeLogStrategyRequest request) {

        String tags = request.getTags();

        String[] tagArr = tags.split(";");

        String resourceId = StringUtils.substringAfter(tagArr[0], "resourceId=");
        Set<String> allNodeIds = new LinkedHashSet<>();
        //将管理节点也计算在内,OneStor没有节点id
        if(!request.getPlatform().equals(ReportResourceEnum.onestor.name())){
            allNodeIds.add("0");
        }


        Set<String> hostIds;
        if (tagArr.length == 1) {
            hostIds = getHostApi(request.getPlatform()).queryHostIds(resourceApi.findRestHostByResourceId(resourceId));
        } else {
            String hostIdsStr = StringUtils.substringAfter(tagArr[1], "hostIds=");
            hostIds = Stream.of(hostIdsStr.split(",")).collect(Collectors.toSet());
        }

        Set<String> logPaths = request.getLogs().stream().filter(s -> StringUtils.isNotEmpty(s.getTargetType())).map(RealTimeLogStrategyLogs::getLogPath).collect(Collectors.toSet());

        if(CollectionUtils.isEmpty(logPaths)){
            return Optional.empty();
        }

        allNodeIds.addAll(hostIds);

        RealTimeLogStrategy realTimeLogStrategy = RealTimeLogStrategy.builder()
                .logPaths(logPaths)
                .platform(request.getPlatform())
                .resourceId(resourceId)
                .targets(allNodeIds)
                .build();

        return Optional.of(realTimeLogStrategy);
    }

    @Override
    public void handleIncreasedRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request) {

        //保存LogMetaData
        Set<LogMetaData> logMetaData = request.stream().map(RealTimeLogStrategyRequest::getLogs).flatMap(Collection::stream).map(LogMetaData::convert).collect(Collectors.toSet());
        refreshLogMetaData(logMetaData);

        //数据转换
        List<RealTimeLogStrategy> strategiesToDo = request.stream().map(this::convert).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Map<String, RealTimeLogStrategy> todoMap = strategiesToDo.stream().collect(Collectors.toMap(RealTimeLogStrategy::getResourceId, a -> a, (a, b) -> b));


        List<RealTimeLogStrategy> strategiesAlreadyHave = mongoTemplate.findAll(RealTimeLogStrategy.class);
        Map<String, RealTimeLogStrategy> haveMap = strategiesAlreadyHave.stream().collect(Collectors.toMap(RealTimeLogStrategy::getResourceId, a -> a, (a, b) -> b));

        todoMap.putAll(haveMap);
        handleRealTimeLogStrategy(todoMap, haveMap);
    }

    @Override
    public void handleRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request) {

        //保存LogMetaData
        Set<LogMetaData> logMetaData = request.stream().map(RealTimeLogStrategyRequest::getLogs).flatMap(Collection::stream).map(LogMetaData::convert).collect(Collectors.toSet());
        refreshLogMetaData(logMetaData);

        //数据转换
        List<RealTimeLogStrategy> strategiesToDo = request.stream().map(this::convert).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Map<String, RealTimeLogStrategy> todoMap = strategiesToDo.stream().collect(Collectors.toMap(RealTimeLogStrategy::getResourceId, a -> a, (a, b) -> b));


        List<RealTimeLogStrategy> strategiesAlreadyHave = mongoTemplate.findAll(RealTimeLogStrategy.class);
        Map<String, RealTimeLogStrategy> haveMap = strategiesAlreadyHave.stream().collect(Collectors.toMap(RealTimeLogStrategy::getResourceId, a -> a, (a, b) -> b));


        handleRealTimeLogStrategy(todoMap, haveMap);
    }

    private void handleRealTimeLogStrategy(Map<String, RealTimeLogStrategy> todoMap, Map<String, RealTimeLogStrategy> haveMap){
        Set<String> resourceIdTodo = todoMap.keySet();
        Set<String> resourceIdHave = haveMap.keySet();
        //新增的资源
        Set<String> resourceAdd = resourceIdTodo.stream().filter(resourceId -> !resourceIdHave.contains(resourceId)).collect(Collectors.toSet());
        resourceAdd.stream().map(todoMap::get).forEach(toStartup -> CloudExecutorServices.get().getIoBusyService().execute(() -> {
            String key = String.format(Constant.LockKey.KEY_REALTIME_LOG, toStartup.getResourceId());
            String token = lockApi.acquire(key, TimeUnit.MINUTES.toMillis(10));
            try {
                //获取到锁
                if (StringUtils.isNotEmpty(token)) {
                    log.warn("[filebeat-startup] resource {} acquired lock, token is {}", toStartup.getResourceId(), token);
                    boolean result = handleRealTimeLog(toStartup, Constant.OPERATE_STARTUP);
                    if (result) {
                        mongoTemplate.save(toStartup);
                    }
                } else {
                    log.warn("[filebeat-startup] resource {} is operating, skip", toStartup.getResourceId());
                }
            } finally {
                lockApi.release(key, token);
            }
        }));


        //减少的资源
        Set<String> resourceDelete = resourceIdHave.stream().filter(resourceId -> !resourceIdTodo.contains(resourceId)).collect(Collectors.toSet());
        resourceDelete.stream().map(haveMap::get).forEach(toShutdown -> CloudExecutorServices.get().getIoBusyService().execute(() -> {
            String key = String.format(Constant.LockKey.KEY_REALTIME_LOG, toShutdown.getResourceId());
            String token = lockApi.acquire(key, TimeUnit.MINUTES.toMillis(1));
            try {
                if (StringUtils.isNotEmpty(token)) {
                    log.warn("[filebeat-shutdown] resource {} acquired lock, token is {}", toShutdown.getResourceId(), token);
                    boolean result = handleRealTimeLog(toShutdown, Constant.OPERATE_SHUTDOWN);
                    if (result) {
                        Query query = new Query(Criteria.where("resourceId").is(toShutdown.getResourceId()));
                        mongoTemplate.remove(query, RealTimeLogStrategy.class);
                    }
                } else {
                    log.warn("[filebeat-shutdown] resource {} is operating, skip", toShutdown.getResourceId());
                }
            } finally {
                lockApi.release(key, token);
            }
        }));

        //不变的资源
        Set<String> resources = resourceIdHave.stream().filter(resourceIdTodo::contains).collect(Collectors.toSet());

        //判断主机是不是有删减
        resources.forEach(resourceId -> CloudExecutorServices.get().getIoBusyService().execute(() -> {
            String key = String.format(Constant.LockKey.KEY_REALTIME_LOG, resourceId);
            String token = lockApi.acquire(key, TimeUnit.MINUTES.toMillis(1));
            try {
                if (StringUtils.isNotEmpty(token)) {
                    log.warn("[filebeat-update] resource {} acquired lock, token is {}", resourceId, token);
                    RealTimeLogStrategy todo = todoMap.get(resourceId);
                    RealTimeLogStrategy have = haveMap.get(resourceId);
                    boolean result = handleRealTimeLog(todo, have);
                    if (result) {
                        Query query = new Query(Criteria.where("resourceId").is(resourceId));
                        Update update = new Update().set("targets", todo.getTargets()).set("logPaths", todo.getLogPaths());
                        mongoTemplate.updateMulti(query, update, RealTimeLogStrategy.class);
                    }
                } else {
                    log.warn("[filebeat-update] resource {} is operating, skip", resourceId);
                }
            } finally {
                lockApi.release(key, token);
            }
        }));
    }

    private boolean handleRealTimeLog(RealTimeLogStrategy todo, RealTimeLogStrategy have) {
        Set<String> todoTargets = todo.getTargets();
        Set<String> haveTargets = have.getTargets();

        //新增的主机
        Set<String> targetsAdd = todoTargets.stream().filter(target -> !haveTargets.contains(target)).collect(Collectors.toSet());
        Set<String> targetsAddSuccess = handleRealTimeLog(todo, targetsAdd, Constant.OPERATE_STARTUP);

        //删除的主机
        Set<String> targetsDelete = haveTargets.stream().filter(target -> !todoTargets.contains(target)).collect(Collectors.toSet());
        Set<String> targetsDeleteSuccess = handleRealTimeLog(have, targetsDelete, Constant.OPERATE_SHUTDOWN);

        //不变的主机
        Set<String> targets = haveTargets.stream().filter(todoTargets::contains).collect(Collectors.toSet());
        Set<String> logPathTodo = todo.getLogPaths();
        Set<String> logPathHave = have.getLogPaths();
        //主机不变但日志有变化,执行启动操作
        boolean notChange = logPathHave.size() == logPathTodo.size() && logPathHave.containsAll(logPathTodo);
        if (!notChange) {
            handleRealTimeLog(todo, targets, Constant.OPERATE_STARTUP);
        }

        //删除失败的主机+添加成功的主机就是要保存的主机
        Set<String> targetsDeleteFail = targetsDelete.stream().filter(tar -> !targetsDeleteSuccess.contains(tar)).collect(Collectors.toSet());
        targetsAddSuccess.addAll(targetsDeleteFail);
        todo.setTargets(targetsAddSuccess);

        return !CollectionUtils.isEmpty(targetsAddSuccess);
    }


    private boolean handleRealTimeLog(RealTimeLogStrategy strategy, String operate) {

        Set<String> targets = strategy.getTargets().stream().filter(target -> handleRealTimeLog(strategy, target, operate)).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(targets)) {
            return false;
        }
        //仅保存部署成功的target
        strategy.setTargets(targets);
        return true;
    }

    private Set<String> handleRealTimeLog(RealTimeLogStrategy strategy, Set<String> targets, String operate) {
        return targets.stream().filter(target -> handleRealTimeLog(strategy, target, operate)).collect(Collectors.toSet());
    }

    private boolean handleRealTimeLog(RealTimeLogStrategy strategy, String target, String operate) {

        try {
            SSHHost sshHost = getHostApi(strategy.getPlatform()).getHost(resourceApi.findRestHostByResourceId(strategy.getResourceId()), target);

            if (operate.equals(Constant.OPERATE_STARTUP)) {
                try {
                    deployFileBeat(sshHost, strategy, deployApi.queryIps());
                    handleRealTimeLog(strategy, target, Constant.OPERATE_SHUTDOWN);//如果之前已经启动了filebeat，需要先停止一下
                    realtimeLogUpload(sshHost, operate);
                    log.info("[filebeat-startup] success, host is {}", sshHost.getIp());
                } catch (Exception e) {
                    log.error("[filebeat-startup] fail, host is {}", sshHost.getIp(), e);
                }
            }

            if (operate.equals(Constant.OPERATE_SHUTDOWN)) {
                try {
                    realtimeLogUpload(sshHost, operate);
                    log.info("[filebeat-shutdown] success, host is {}", sshHost.getIp());
                } catch (Exception e) {
                    String result = SSHTools.executeNoException(sshHost, "bash /var/lib/filebeat/status.sh");
                    if (!StringUtils.contains(result, "shutdown")) {
                        log.error("[filebeat-shutdown] fail, host is {}", sshHost.getIp(), e);
                        throw e;
                    }
                    log.info("[filebeat-shutdown] success, host is {}", sshHost.getIp());
                }
            }
            return true;
        } catch (Exception e) {
            log.error("[filebeat-{}] operate filebeat fail, resourceId is {}, target is {}, operate is {}", operate, strategy.getResourceId(), target, operate, e);
            return false;
        }
    }

    public void deployFileBeat(SSHHost sshHost, RealTimeLogStrategy realTimeLogStrategy, Set<String> kafkaServerHost) {
        // 拷贝一份filebeat副本进行操作
        duplicateThreadFilebeat();

        //1 修改filebeat配置文件
        modifyFileBeatYml(sshHost, realTimeLogStrategy, kafkaServerHost);

        //2 拷贝filebeat安装包到主机
        copyFileBeat(sshHost);

        //3 合法性检测  ./filebeat test -e output
        validFileBeat(sshHost);

        // 删除当前线程所用的filebeat副本
        deleteThreadFilebeat();
    }

    @SneakyThrows
    private void validFileBeat(SSHHost sshHost) {
        String cmd = "/var/lib/filebeat/filebeat-8.0.1-linux-x86_64/filebeat test -e output -c /var/lib/filebeat/filebeat-8.0.1-linux-x86_64/filebeat-watcher.yml";
        String result = SSHTools.execute(sshHost, cmd);
        log.info("[filebeat-startup] host:{} check valid result:{}", sshHost.getIp(), result);
    }

    @SneakyThrows
    private void copyFileBeat(SSHHost sshHost) {
        StringBuilder scpCmd = new StringBuilder();
        //onestor的SDS_Admin用户默认无法访问/var/lib，所以要用sudo赋予其访问权限
        if(sshHost.getPlatform().equals(ReportResourceEnum.onestor.name())){
            SSHTools.executeNoException(sshHost, "sudo chmod 777 /var/lib/*");
            SSHTools.executeNoException(sshHost, "sudo chmod 777 /var/log/*");
        }
        scpCmd.append("sudo sshpass -p ")
                .append(sshHost.getPassword())
                .append(" scp -r ")
                .append(getThreadFileBeatPath() + "/filebeat")
                .append(" ")
                .append(sshHost.getUser())
                .append("@")
                .append(sshHost.getIp())
                .append(":")
                .append("/var/lib");
        FuncUtil.runCommandThrowException(new String[]{"sh", "-c", scpCmd.toString()}, (int) TimeUnit.HOURS.toMillis(1));

        String cmd = "chmod -R +x /var/lib/filebeat/";
        SSHTools.execute(sshHost, cmd);
        log.info("[filebeat-startup] host:{} scp filebeat package success", sshHost.getIp());
    }

    private String getFileBeatYmlPath() {
        String watcherHome = FuncUtil.runCommand(new String[]{"sh", "-c", "cat /etc/watcher_home"}, (int) TimeUnit.HOURS.toMillis(1));
        watcherHome = watcherHome.replace("\n", "");

        return Paths.get(watcherHome, "components", "filebeat-" + Thread.currentThread().getName(), "filebeat", "filebeat-8.0.1-linux-x86_64", "filebeat-watcher.yml").toString();
    }

    private String getFileBeatPath() {
        String watcherHome = FuncUtil.runCommand(new String[]{"sh", "-c", "cat /etc/watcher_home"}, (int) TimeUnit.HOURS.toMillis(1));
        watcherHome = watcherHome.replace("\n", "");
        return Paths.get(watcherHome, "components", "filebeat").toString();
    }

    private String getThreadFileBeatPath(){
        String watcherHome = FuncUtil.runCommand(new String[]{"sh", "-c", "cat /etc/watcher_home"}, (int) TimeUnit.HOURS.toMillis(1));
        watcherHome = watcherHome.replace("\n", "");
        return Paths.get(watcherHome, "components", "filebeat-" + Thread.currentThread().getName()).toString();
    }

    private void duplicateThreadFilebeat(){
        FuncUtil.runCommand(new String[]{"sh", "-c", String.format("mkdir -p %s/filebeat", getThreadFileBeatPath())}, (int) TimeUnit.HOURS.toMillis(1));
        FuncUtil.runCommand(new String[]{"sh", "-c", String.format("cp %s -a %s/", getFileBeatPath(), getThreadFileBeatPath())}, (int) TimeUnit.HOURS.toMillis(1));
    }

    private void deleteThreadFilebeat(){
        FuncUtil.runCommand(new String[]{"sh", "-c", String.format("rm -rf %s", getThreadFileBeatPath())}, (int) TimeUnit.HOURS.toMillis(1));
    }

    @SneakyThrows
    private void modifyFileBeatYml(SSHHost sshHost, RealTimeLogStrategy realTimeLogStrategy, Set<String> kafkaServerHost) {

        Yaml filebeatYml = new Yaml();
        String ymlPath = getFileBeatYmlPath();
        Map<String, Object> config = filebeatYml.load(new FileReader(ymlPath));

        LinkedHashMap<String, Object> inputConfig = new LinkedHashMap<>();
        inputConfig.put("type", "log");
        inputConfig.put("enabled", true);
        inputConfig.put("paths", new ArrayList<>(realTimeLogStrategy.getLogPaths()));
        inputConfig.put("multiline.type", "pattern");
        //匹配正常日志行开头：熟悉开头/月份英文开头(message、ovs-vswitchd.log)/中括号开头(uis.log),不匹配的行合并到上一正常行
        inputConfig.put("multiline.pattern", "^[0-9]|^Jan|^Feb|^Mar|^Apr|^May|^Jun|^Jul|^Aug|^Sep|^Oct|^Nov|^Dec|^\\[");
        inputConfig.put("multiline.negate", true);//true:匹配不符合正则的行，false:匹配符合正则的行
        inputConfig.put("multiline.match", "after");//after:不正常的行要接在上一正常行的后面
        inputConfig.put("tail_files", "true");//true:filebeat服务启动后读取文件新产生的行;false:服务启动后从文件开头开始读取

        config.put("filebeat.inputs", Collections.singletonList(inputConfig));

        LinkedHashMap<String, Object> kafkaConfig = new LinkedHashMap<>();
        kafkaConfig.put("hosts", kafkaServerHost.stream().map(k -> k + ":9092").collect(Collectors.toList()));
        kafkaConfig.put("topic", Constant.KAFKA_TOPIC_FILEBEAT);

        config.put("output.kafka", kafkaConfig);

        LinkedHashMap<String, Object> ownFieldsConfig = new LinkedHashMap<>();
        ownFieldsConfig.put("tags", getTags(sshHost));
        ownFieldsConfig.put("platform", sshHost.getPlatform());

        config.put("fields", ownFieldsConfig);//添加自定义字段

        LinkedHashMap<String, Object> dropFieldsConfig = new LinkedHashMap<>();
        dropFieldsConfig.put("fields", Arrays.asList("input", "agent", "ecs"));//忽略某些filebeat自带字段，减少每条消息的字段数
        dropFieldsConfig.put("ignore_missing", false);
        LinkedHashMap<String, Object> processorsConfig = new LinkedHashMap<>();
        processorsConfig.put("drop_fields", dropFieldsConfig);

        config.put("processors", Collections.singletonList(processorsConfig));

        filebeatYml.dump(config, new FileWriter(ymlPath));

        log.info("[filebeat-startup] host:{} modify filebeat config success", sshHost.getIp());
    }

    private Object getTags(SSHHost sshHost) {
        StringBuffer sb = new StringBuffer();
        sb.append("resourceId=").append(sshHost.getResourceId()).append(";")
                .append("hostId=").append(sshHost.getHostId()).append(";")
                .append("hostIP=").append(sshHost.getIp()).append(";")
                .append("hostName=").append(sshHost.getHostName()).append(";");
        return sb.toString();
    }

    @SneakyThrows
    @Override
    public void realtimeLogUpload(SSHHost sshHost, String operate) {
        switch (operate) {
            case Constant.OPERATE_STARTUP:
                //stratup.sh已修改，目前可以直接调用脚本并进行后续代码。
                SSHTools.executeSshCmd(SSHTools.createSession(sshHost), "/var/lib/filebeat/startup.sh");
                break;
            case Constant.OPERATE_SHUTDOWN:
                SSHTools.executeSshCmd(SSHTools.createSession(sshHost), "/var/lib/filebeat/shutdown.sh");
                break;
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stream.of(hostApis).forEach(collectApi -> {
            ReportResourceEnum resource = collectApi.whoAreYou();
            hostApiMap.put(resource, collectApi);
        });

        Stream.of(logPatternApis).forEach(collectApi -> {
            RealTimeLogTypeEnum logType = collectApi.logType();
            logPatternApiMap.put(logType.name(), collectApi);
        });
    }

    private HostApi getHostApi(String platform) {
        return Optional.ofNullable(hostApiMap.get(ReportResourceEnum.valueOf(platform))).orElseThrow(() -> new AppException(ErrorCodes.NOT_FOUND));
    }

    private void refreshLogMetaData(Set<LogMetaData> logMetaData) {
        logMetaData.forEach(logMetaData1 -> {
            Query query = new Query(Criteria.where("logPath").is(logMetaData1.getLogPath()));
            boolean exist = mongoTemplate.exists(query, LogMetaData.class);
            if (exist) {
                Update update = new Update()
                        .set("logType", logMetaData1.getLogType())
                        .set("targetType", logMetaData1.getTargetType())
                        ;
                mongoTemplate.updateMulti(query, update, LogMetaData.class);
            } else {
                mongoTemplate.save(logMetaData1);
            }
        });

        logPathLogType.clear();
        logPathTargetType.clear();
    }

    private LogPatternApi getLogPatternApi(String logType) {
        try {
            if(StringUtils.isEmpty(logType)){
                return logPatternApiMap.get(RealTimeLogTypeEnum.others_.name());
            }
            return Optional.ofNullable(logPatternApiMap.get(logType)).orElse(logPatternApiMap.get(RealTimeLogTypeEnum.others_.name()));
        } catch (Exception e) {
            return logPatternApiMap.get(RealTimeLogTypeEnum.others_.name());
        }
    }

    @Override
    public Optional<LogLine> parseLine(String logType, String message) {
        LogPatternApi logPatternApi =  getLogPatternApi(logType);
        if(Objects.isNull(logPatternApi)){
            LogLine logLine = new LogLine();
            logLine.setMessage(message);
            return Optional.ofNullable(logLine);
        }
        return logPatternApi.parseLine(message);
    }

    @Override
    public Map<String, String> queryLogPathTargetType(){
        if(!logPathTargetType.isEmpty()){
            return logPathTargetType;
        }
        List<LogMetaData> logMetaData = mongoTemplate.findAll(LogMetaData.class);
        Map<String, String> map = new ConcurrentHashMap<>();
        logMetaData.forEach(logMetaData1 -> map.put(logMetaData1.getLogPath(), logMetaData1.getTargetType()));
        logPathTargetType = map;
        return logPathTargetType;
    }

    @Override
    public Map<String, String> queryLogPathLogType(){
        if(!logPathLogType.isEmpty()){
            return logPathLogType;
        }
        List<LogMetaData> logMetaData = mongoTemplate.findAll(LogMetaData.class);
        Map<String, String> map = new ConcurrentHashMap<>();
        logMetaData.forEach(logMetaData1 -> map.put(logMetaData1.getLogPath(), logMetaData1.getLogType()));
        logPathLogType = map;
        return logPathLogType;
    }

    @Override
    public void createAndConsumeFilebeatLogTopic(int numPartitions, int replicationFactor) {
        try {
            kafkaConsole.createTopic(Constant.KAFKA_TOPIC_FILEBEAT, numPartitions, (short) replicationFactor);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[kafka] create topic fail", e);
        }

        try {
            kafkaConsumers.receiveFilebeatMsg();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[kafka] start consume fail", e);
        }
    }
    @Override
    public void handleFilebeatCheck() {
        log.info("[filebeat-check] start to check filebeat status");
        List<RealTimeLogStrategy> strategies = this.mongoTemplate.findAll(RealTimeLogStrategy.class);
        strategies.forEach(strategy -> CloudExecutorServices.get().getFilebeatService().execute(() ->{
            handleFilebeatCheck(strategy);
        }));
        log.info("[filebeat-check] finish to check filebeat status");
    }

    private void handleFilebeatCheck(RealTimeLogStrategy strategy){
        strategy.getTargets().forEach(target -> CloudExecutorServices.get().getFilebeatTargetService().execute(() -> {
            String operate = Constant.OPERATE_STARTUP;
            try {
                SSHHost sshHost = getHostApi(strategy.getPlatform()).getHost(resourceApi.findRestHostByResourceId(strategy.getResourceId()), target);
                log.info("[filebeat-check] start filebeat checking,resourceId is {}, host is {}", strategy.getResourceId(),sshHost.getIp());
                String result = SSHTools.executeNoException(sshHost, "bash /var/lib/filebeat/status.sh");
                if (StringUtils.contains(result, "shutdown")) {
                    log.info("[filebeat-check] filebeat is shutdown, host is {}", sshHost.getIp());
                    try {
                        realtimeLogUpload(sshHost, operate);
                        log.info("[filebeat-startup] success, host is {}", sshHost.getIp());
                    } catch (Exception e) {
                        log.error("[filebeat-startup] fail, host is {}", sshHost.getIp(), e);
                    }
                }
                log.info("[filebeat-check] finish filebeat checking,resourceId is {}, host is {}", strategy.getResourceId(),sshHost.getIp());
            } catch (Exception e) {
                log.error("[filebeat-{}] operate filebeat fail, resourceId is {}, target is {}, operate is {}", operate, strategy.getResourceId(), target, operate, e);
            }
        }));
    }

    @Override
    public void deleteResourceRealTimeLog(List<String> resourceIds) {
        List<RealTimeLogStrategy> strategiesAlreadyHave = mongoTemplate.findAll(RealTimeLogStrategy.class);
        Map<String, RealTimeLogStrategy> haveMap = strategiesAlreadyHave.stream().collect(Collectors.toMap(RealTimeLogStrategy::getResourceId, a -> a, (a, b) -> b));
        //减少的资源
        Set<String> resourceDelete = resourceIds.stream().collect(Collectors.toSet());
        resourceDelete.stream().map(haveMap::get).filter(Objects::nonNull).forEach(toShutdown -> CloudExecutorServices.get().getIoBusyService().execute(() -> {
            String key = String.format(Constant.LockKey.KEY_REALTIME_LOG, toShutdown.getResourceId());
            String token = lockApi.acquire(key, TimeUnit.MINUTES.toMillis(1));
            try {
                if (StringUtils.isNotEmpty(token)) {
                    log.warn("[filebeat-shutdown] resource {} acquired lock, token is {}", toShutdown.getResourceId(), token);
                    boolean result = handleRealTimeLog(toShutdown, Constant.OPERATE_SHUTDOWN);
                    if (result) {
                        Query query = new Query(Criteria.where("resourceId").is(toShutdown.getResourceId()));
                        mongoTemplate.remove(query, RealTimeLogStrategy.class);
                    }
                } else {
                    log.warn("[filebeat-shutdown] resource {} is operating, skip", toShutdown.getResourceId());
                }
            } finally {
                lockApi.release(key, token);
            }
        }));
    }

    @Override
    public void clearCache() {
        logPathTargetType.clear();
        logPathLogType.clear();
    }

    @Override
    public List<LogLine> searchAll(String platform, Long resourceId, String type, Long targetId, String path, String queryString, Long startTime, Long endTime, Integer sortDir, String sortField, Integer logNum, String level) {
        try {

            String indexName = Constant.RealtimeLog.ES_INDEX_NAME_LOG;
            // 查询条件
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if(StringUtils.isNotBlank(queryString)){
                MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("message", queryString);
                queryBuilder.operator(Operator.OR);
                boolQuery.must(queryBuilder);
            }

            if(Objects.nonNull(startTime) && Objects.nonNull(endTime)){
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("timestamp")
                        .from(startTime)
                        .to(endTime);
                boolQuery.filter(rangeQuery);
            }

            if(StringUtils.isNotBlank(sortField)){
                sourceBuilder.sort(sortField, Objects.equals(sortDir, 0)? SortOrder.ASC : SortOrder.DESC);
            }

            // 分页查询
            sourceBuilder.query(boolQuery);
            sourceBuilder.from(0);
            if(Objects.nonNull(logNum)){
                sourceBuilder.size(logNum);
            } else {
                sourceBuilder.size(10);
            }
            List<String> list = esOperation.listString(indexName, sourceBuilder);
            List<LogLine> logLines = new ArrayList<>();
            for (String s : list) {
                logLines.add(SerializeUtils.json2Object(s, LogLine.class));
            }
            return logLines;
        } catch(Exception e) {
            log.info("[log-search] search error:{}",e.getMessage());
            throw new AppException(ErrorCodes.REST_FAIL);
        }
    }


    private String generateQueryString(String watcherCode, String platform, Long resourceId, String type, Long targetId, String path, String queryString, Long startTime, Long endTime) {
        if(null == startTime || null == endTime || StringUtils.isEmpty(platform)) {
            throw new AppException(ErrorCodes.REST_FAIL);
        }
        String result = "time:>=" + startTime + " AND " + "time:<=" + endTime + " AND " + "platform:" + platform;
        if(StringUtils.isNotEmpty(watcherCode)) {
            result += " AND " + "watcherCode:" + watcherCode;
        }
        if(null != resourceId) {
            result += " AND " + "resourceId:" + resourceId;
        }
        if(StringUtils.isNotEmpty(type)) {
            result += " AND " + "targetType:" + type;
        }
        if(null != targetId) {
            if(Constant.Log.HOST.equals(type)) {
                result += " AND " + "hostId:" + targetId;
            } else if(Constant.Log.VM.equals(type)) {
            } else if(Constant.Log.TERMINAL.equals(type)) {
            }
        }
        if(StringUtils.isNotEmpty(path)) {
            result += " AND " + "path:" + path;
        }
        if(StringUtils.isNotEmpty(queryString)) {
            result += " AND " + queryString;
        }
        return result;
    }
}
