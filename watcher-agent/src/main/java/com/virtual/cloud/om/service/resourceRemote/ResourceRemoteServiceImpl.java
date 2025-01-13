package com.virtual.cloud.om.service.resourceRemote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;

import com.virtual.cloud.om.entity.ResourceEntity;
import com.virtual.cloud.om.entity.ResourceRemote;
import com.virtual.cloud.om.entity.ResourceRemoteOamServer;
import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.repository.ResourceRemoteOamServerRepository;
import com.virtual.cloud.om.repository.ResourceRemoteRepository;
import com.virtual.cloud.om.repository.ResourceRepository;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.service.resource.ResourceService;
import com.virtual.cloud.om.service.ssh.ResourceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/9/2 11:20
 */
@Service
@Slf4j
public class ResourceRemoteServiceImpl implements ResourceRemoteService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private  ResourceRepository resourceRepository;

    @Resource
    private ResourceRemoteRepository resourceRemoteRepository;

    @Resource
    private ResourceRemoteOamServerRepository resourceRemoteOamServerRepository;

    @Resource
    private TaskMgrApi taskMgrApi;

    @Resource
    private ResourceAuthService resourceAuthService;

    @Override
    public RpcPagingLoadResult<ResourceRemoteDTO> selectAllResourceInfo(Integer page,Integer size,String platform, String resourceName,String ipAddress) {
        Query query=new Query();
        Pageable pageable = PageRequest.of(page,size);
        query.with(pageable);
        if (StringUtils.isNotEmpty(platform)) {
            query.addCriteria( Criteria.where("platform").is(platform));
        }
        if (StringUtils.isNotEmpty(resourceName)){
            String pattern_name=resourceName;
            Pattern pattern =Pattern.compile("^.*"+pattern_name+".*$",Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("resourceName").regex(pattern));
        }
        if (StringUtils.isNotEmpty(ipAddress)){
            String pattern_ipAddress=ipAddress;
            Pattern pattern =Pattern.compile("^.*"+pattern_ipAddress+".*$",Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("ipAddress").regex(pattern));
        }
        List<ResourceRemote> resourceRemotes = mongoTemplate.find(query, ResourceRemote.class);
        long count = mongoTemplate.count(query, ResourceRemote.class);
        List<ResourceRemoteDTO> resourceRemoteDTOS = resourceRemotes.parallelStream().map(s -> {
            ResourceRemoteDTO resourceRemoteDTO = new ResourceRemoteDTO();
            BeanUtils.copyProperties(s, resourceRemoteDTO);
            resourceRemoteDTO.setUsable(1);
            Boolean sshType =true;
            ResourceEntity entity = resourceRepository.findById(s.getResourceId());
            List<ResourceRemoteOamServer> oamResourceServer = resourceRemoteOamServerRepository.findByResourceId(resourceRemoteDTO.getResourceId());
            try {
                 sshType = resourceAuthService.getSshType(entity);
                if (sshType){
                    /**
                     * 补偿机制用于定时任务执行之时系统宕机
                     */
                    if (s.getEndTime() > System.currentTimeMillis()) {
                        resourceRemoteDTO.setRemote(Constant.Websocket.SSH_REMOTE_OPEN);
                        s.setRemote(Constant.Websocket.SSH_REMOTE_OPEN);
                    } else {
                        if (StrUtil.isEmpty(s.getUsername()) && StrUtil.isEmpty(s.getPassword())) {
                            resourceRemoteDTO.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                            s.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                            s.setEndTime(System.currentTimeMillis());
                        }else {
                            resourceAuthService.modifyUserSshAuth(false, resourceRemoteDTO.getUsername(),resourceRemoteDTO.getPassword(), entity);
                            closeRemoteLocal(s,resourceRemoteDTO,oamResourceServer);
                        }
                    }
                }else {
                    resourceRemoteDTO.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                    s.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                }
            } catch (AppException e) {
                if (Objects.equals(s.getRemote(),Constant.Websocket.SSH_REMOTE_OPEN)){
                    if (s.getEndTime() > System.currentTimeMillis()) {
                        resourceRemoteDTO.setRemote(s.getRemote());
                    } else {
                        closeRemoteLocal(s,resourceRemoteDTO,oamResourceServer);
                    }
                }else {
                    resourceRemoteDTO.setRemote(s.getRemote());
                }
                if (Objects.equals(e.getErrorCode(),ErrorCodes.REOSURE_NO_USABLE)){
                    resourceRemoteDTO.setUsable(0);
                    resourceRemoteDTO.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                    s.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
                    resourceRemoteDTO.setEndTimeStr(null);
                    s.setEndTimeStr(null);
                }
            }
            resourceRemoteDTO.setUsername(null);
            resourceRemoteDTO.setPassword(null);
            if (Objects.equals(s.getRemote(),Constant.Websocket.SSH_REMOTE_CLOSE)){
                resourceRemoteDTO.setEndTimeStr(null);
                s.setEndTimeStr(null);
            }
            return resourceRemoteDTO;
        }).collect(Collectors.toList());
        resourceRemotes.forEach(s->{
            mongoTemplate.save(s);
        });
        RpcPagingLoadResult<ResourceRemoteDTO> result = new RpcPagingLoadResult<>(resourceRemoteDTOS);
        result.setTotalLength(Math.toIntExact(count));
        return result;
    }





    @Override
    public void modifyResourceInfo(ResourceRemoteDTO remoteDTO) {
        Long strartTime =System.currentTimeMillis();
        ResourceRemote  resourId = resourceRemoteRepository.findByResourceId(remoteDTO.getResourceId());
        ResourceEntity byId = resourceRepository.findById(remoteDTO.getResourceId());
        List<ResourceRemoteOamServer> oamResourceServer = resourceRemoteOamServerRepository.findByResourceId(remoteDTO.getResourceId());
        log.info("[RESOURCEREMOTE] agent begin modify  ssh auth   resourceId : {}",remoteDTO.getResourceId());

        Boolean flag =false;
        Integer remote = remoteDTO.getRemote();
        resourId.setRemote(remote);
        if (Objects.equals(remote,Constant.Websocket.SSH_REMOTE_OPEN)){
            flag=true;
            resourId.setRemoteType(remoteDTO.getRemoteType());
            switch (remoteDTO.getRemoteType()){
                case Constant.Websocket.SSH_WATCHER_REMOTE_TYPE_SEVEN_DAY:
                    resourId.setEndTime(strartTime+Constant.Websocket.SEVEN_DAY);
                    break;
                case Constant.Websocket.SSH_WATCHER_REMOTE_TYPE_THREE_DAY:
                    resourId.setEndTime(strartTime+Constant.Websocket.THREE_DAY);
                    break;
                default:
                    resourId.setEndTime(strartTime+Constant.Websocket.ONE_DAY_MILL);
                    break;
            }
            String fullDateTime = Utils.formatFullDateTime(resourId.getEndTime());
            resourId.setEndTimeStr(fullDateTime);
            try {
                this.addCloseTask(remoteDTO ,resourId);
            } catch (Exception e) {
               log.error("add close ssh task fail: {}",e);
            }
        }else {
            resourId.setRemoteType(Constant.Websocket.SSH_REMOTE_TYPE_CLOSE);
            resourId.setEndTime(System.currentTimeMillis());
//            resourId.setEndTimeStr(Utils.formatFullDateTime(resourId.getEndTime()));
        }


        try {
            Boolean modifyFlag = resourceAuthService.modifyUserSshAuth(flag,remoteDTO.getUsername(),remoteDTO.getPassword(),byId);
            if (!modifyFlag){
                throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
            }
            mongoTemplate.save(resourId);
            if (CollectionUtil.isNotEmpty(oamResourceServer)){
                this.modifyOamServer(oamResourceServer,resourId);
            }
        } catch (Exception e) {
            //接口调失败
            log.error("modify SSH_AUTH fail :{}",e);
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }

    }


    private void addCloseTask(ResourceRemoteDTO remoteDTO,ResourceRemote  resourId){
        TaskDTO queryByName = taskMgrApi.queryByName(Task.TASK_SSH_CLOSE + "_" + resourId.getResourceId());
        if (Objects.nonNull(queryByName)){
            taskMgrApi.delete(queryByName);
        }
        TaskDTO taskDTO=new TaskDTO();
        taskDTO.setTaskName(Task.TASK_SSH_CLOSE+"_"+resourId.getResourceId());
        taskDTO.setId(Task.TASK_SSH_CLOSE+"_"+resourId.getResourceId());
        taskDTO.setAvailableStartTime(resourId.getEndTime());
        taskDTO.setTaskType(Task.TASK_SSH_CLOSE);
        taskDTO.setId(Task.TASK_SSH_CLOSE+"_"+resourId.getResourceId());
        taskDTO.setDescription(Task.TASK_SSH_CLOSE);
        taskDTO.setCycleType(Task.CYCLE_TYPE_ONCE);
        taskDTO.setCycleTime(Utils.formatFullDateTime(resourId.getEndTime()));
        taskDTO.setData(remoteDTO);
        taskMgrApi.addTask(taskDTO);
    }
    /**
     * 更新oam的状态
     * @param oamResourceServer
     * @param resourceRemote
     */
    @Override
    public void  modifyOamServer(List<ResourceRemoteOamServer> oamResourceServer,ResourceRemote resourceRemote){
        oamResourceServer.forEach(s -> {
            s.setEndTime(resourceRemote.getEndTime());
            s.setRemoteType(resourceRemote.getRemoteType());
            s.setUseTime(Utils.formatFullDateTime(resourceRemote.getEndTime()));
            mongoTemplate.save(s);
        });

    }

    @Override
    public void checkUserSshAuth(ResourceRemoteDTO remoteDTO){
        Boolean flag=false;
        ResourceEntity byId = resourceRepository.findById(remoteDTO.getResourceId());
        flag = resourceAuthService.checkUserSshAuth(remoteDTO.getUsername(),remoteDTO.getPassword(), byId);
        ResourceRemote resourId = resourceRemoteRepository.findByResourceId(remoteDTO.getResourceId());
        resourId.setUsername(remoteDTO.getUsername());
        resourId.setPassword(remoteDTO.getPassword());
        mongoTemplate.save(resourId);
        if (!flag){
            throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH_FAIL);
        }
    }


    private void closeRemoteLocal(ResourceRemote resourceRemote,ResourceRemoteDTO remoteDTO,List<ResourceRemoteOamServer> oamResourceServer){
        resourceRemote.setRemoteType(Constant.Websocket.SSH_REMOTE_TYPE_CLOSE);
        resourceRemote.setEndTime(System.currentTimeMillis());
        resourceRemote.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
        resourceRemote.setEndTimeStr(Utils.formatFullDateTime(remoteDTO.getEndTime()));
        remoteDTO.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
        if (CollectionUtil.isNotEmpty(oamResourceServer)){
            modifyOamServer(oamResourceServer,resourceRemote);
        }
    }
    @Override
    public void closeSsh(ResourceRemoteDTO resourceRemoteDTO){
        ResourceEntity byId = resourceRepository.findById(resourceRemoteDTO.getResourceId());
        ResourceRemote byResourceId = resourceRemoteRepository.findByResourceId(resourceRemoteDTO.getResourceId());
        List<ResourceRemoteOamServer> oamResourceServer = resourceRemoteOamServerRepository.findByResourceId(resourceRemoteDTO.getResourceId());
        resourceAuthService.modifyUserSshAuth(false, resourceRemoteDTO.getUsername(),resourceRemoteDTO.getPassword(), byId);
        byResourceId.setRemoteType(Constant.Websocket.SSH_REMOTE_TYPE_CLOSE);
        byResourceId.setEndTime(System.currentTimeMillis());
        byResourceId.setRemote(Constant.Websocket.SSH_REMOTE_CLOSE);
        byResourceId.setEndTimeStr(Utils.formatFullDateTime(resourceRemoteDTO.getEndTime()));
        mongoTemplate.save(byResourceId);
        if (CollectionUtil.isNotEmpty(oamResourceServer)){
            modifyOamServer(oamResourceServer,byResourceId);
        }
    }



}
