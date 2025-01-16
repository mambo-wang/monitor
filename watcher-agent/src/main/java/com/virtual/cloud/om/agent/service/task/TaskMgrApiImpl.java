package com.virtual.cloud.om.agent.service.task;


import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.task.cron.CronResolver;
import com.virtual.cloud.om.agent.task.cron.CronResolverFactory;
import com.virtual.cloud.om.agent.task.job.JobClassFactory;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service("taskMgrApi")
public class TaskMgrApiImpl implements TaskMgrApi {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    private static final String TASK_GROUP = "TASK";
    private static final String TASK_TIMER_GROUP = "TASK_TIMER";
    private static final String DATE_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

//    private static StringManager sm = StringManager.getManager(TaskMgr.class);

//    @Resource
//    private Scheduler scheduler;
//
    @Resource
    private TaskRepository taskRepository;

    private final String TASK_NAME="{0}-{1}-{2}-classMsgTask";

    /**
     * 任务默认只在主节点执行
     */
    @PostConstruct
    public void initTaskScheduler() {
        log.info("add task of db to scheduler.");
        List<Task> taskList = taskRepository.findAll();
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        for (Task task : taskList) {
            try {
//                这里把所有的任务加入到
//                if(Task.TYPE_CHECK_CONNECT == task.getTaskType()){
//                    addTaskToScheduler(task);
//                    continue;
//                }
//                if (haMgr.isMaster()) {
//                    addTaskToScheduler(task);
//                }
            } catch (Exception e) {
                log.error("add task id={}, name={} to scheduler failure", task.getId(), task.getTaskName(), e);
            }
        }
    }
    public Task convertToEntity(TaskDTO taskDTO){
       Task task =new Task();
        BeanUtils.copyProperties(taskDTO,task);
        return task;
    }
    public TaskDTO convertToDTO(Task task){
       TaskDTO taskDTO =new TaskDTO();
        BeanUtils.copyProperties(task,taskDTO);
        return taskDTO;
    }


    public Task isTaskNameExist(String taskName) {
        Task byName = taskRepository.findByName(taskName);
        return byName;
    }


    public void addTaskToScheduler(Task task) {
        Objects.requireNonNull(task);

        // 1. add job
        String taskId = task.getId();
        String jobName = task.getId();
        String cycleTime = task.getCycleTime();
        Integer cycleDay = task.getCycleDay();

        try {

            //判断下是否已经存在此任务,针对修改任务
            JobKey jobKey = new JobKey(jobName, TASK_GROUP);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail detail = scheduler.getJobDetail(jobKey);
            if (detail == null) {
                detail = JobBuilder.newJob(JobClassFactory.getJobClass(task.getTaskType()))
                        .withIdentity(jobKey)
                        .storeDurably(true)
                        .build();

            }

            scheduler.addJob(detail, false);
            log.debug("add job={} to scheduler success", jobName);

            // 2. add trigger
            CronResolver cronResolver = CronResolverFactory.generateCronResolver(task.getCycleType(),
                    cycleTime, cycleDay);
            if (cronResolver == null) {
                return;
            }
            String triggerName = task.getId();

            TriggerBuilder builder = TriggerBuilder.newTrigger()
                    .forJob(jobKey).startNow()
                    .withIdentity(triggerName, TASK_TIMER_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronResolver.toCronExpress()));

            //触发器要设置时间范围
            if (!Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {
                if (task.getAvailableStartTime() == null) {
                    task.setAvailableStartTime(System.currentTimeMillis());
                }
                // 不是只执行一次时会设置开始时间和结束时间,加判断的目的是为了解决触发器启动的开始时间是过去的时间的话会自动运行一次
                LocalDateTime expiringTime = LocalDateTime
                        .ofInstant(Instant.ofEpochMilli(task.getAvailableStartTime()), ZoneId.systemDefault());
                if (expiringTime.isBefore(LocalDateTime.now())) {
                    builder.startAt(new Date());
                } else {
                    builder.startAt(new Date(task.getAvailableStartTime()));
                }
                builder.endAt(new Date(task.getAvailableEndTime()));
            }

            scheduler.scheduleJob(builder.build());
            log.debug("add trigger={} to scheduler success", triggerName);
        } catch (SchedulerException e) {
            log.error("add task id={} scheduler failure", taskId, e);
        } catch (Exception e) {
            log.error("add job={} to scheduler failure", jobName, e);
        }

    }

    private boolean isExpire(Task task) throws ParseException {
        Date endTime;
        // 不是一次执行的情况
        if (!Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {
            endTime = new Date(task.getAvailableEndTime());
        } else {
            // 一次执行的情况，不会设置结束时间，直接判断定时时间
            SimpleDateFormat sDate = new SimpleDateFormat(DATE_FORMAT_DATE);
            endTime = sDate.parse(task.getCycleTime());
        }
        // 和当期日期比较看触发器的时间是否已经过去
        return endTime.before(new Date());
    }



    @Override
    public TaskDTO queryById(Long id) {
//        Task task = taskRepository.findById(id).orElseThrow(() -> new AppException(ErrorCodes.Mission_does_not_exist));
//
//        //如果已经被删除了，获取title为空，对应的从列表中删除该项
//        Set<TaskObject> taskObjects = task.getTaskObjectList();
//        List<TaskObject> deletes = taskObjects.stream().filter(taskObject -> taskObject.getObjectName() == null)
//                .collect(Collectors.toList());
//        if (!deletes.isEmpty()) {
//            taskObjects.removeAll(deletes);
//        }
//
        return null;
    }

    @Override
    public TaskDTO queryByName(String name) {
        Task byName = taskRepository.findByName(name);
        if (Objects.nonNull(byName)){
            TaskDTO taskDTO = this.convertToDTO(byName);
            return taskDTO;
        }
       return null;
    }

    @Override
    public void addTask(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        if (!Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {
            task.setAvailableEndTime(timeAdjust(task.getAvailableEndTime()));
        }
         addTaskToDB(task);

        // add task to scheduler
        addTaskToScheduler(task);


    }

    @Transactional
    public void addTaskToDB(Task task) {
        //名称是否已经存在
        Task taskNameExist = isTaskNameExist(task.getTaskName());

        if (Objects.nonNull(taskNameExist)) {
            TaskDTO taskDTO = this.convertToDTO(taskNameExist);
            this.delete(taskDTO);
        }

        // add task and task object relations
        taskRepository.save(task);

    }


    @Override
    public void update(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        if (!Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {
            task.setAvailableEndTime(timeAdjust(task.getAvailableEndTime()));
        }
        updateTaskToDB(task);

        updateTaskToScheduler(task);
    }

    @Transactional
    public void updateTaskToDB(Task task) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(task.getId());
        String id = task.getId();
//        Task dbTask = taskRepository.findById(id);
//        .orElseThrow(() -> new AppException(ErrorCodes.Mission_does_not_exist));
//        if (dbTask == null) {
//            log.warn("update task id={} is not found", id);
////            throw new AppException(ErrorCodes.The_task_does_not_exist);
//        }

        // add task and task object relations


        log.info("update task id={}, name={}", id, task.getTaskName());
        taskRepository.save(task);
    }


    private void updateTaskToScheduler(Task task) {
        deleteTaskFromScheduler(task);
        addTaskToScheduler(task);
    }

    @Override
    public void delete(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        log.info("delete task id={}, name={}", task.getId(), task.getTaskName());
        taskRepository.delete(task);

        deleteTaskFromScheduler(task);
    }

    @Override
    public void delete(String taskId) {
        Task task = taskRepository.findById(taskId);
        log.info("delete task id={}, name={}", task.getId(), task.getTaskName());
        taskRepository.delete(task);

        deleteTaskFromScheduler(task);
//        taskRepository.findById(taskId).ifPresent(this::delete);
    }

    @Override
    public void deleteAll() {
        List<Task> all = taskRepository.findTaskResourceIdNotNull();
        taskRepository.deleteTaskResourceIdNotNull();

        all.forEach(this::deleteTaskFromScheduler);
    }

    @Override
    public void deleteAllStrategyTask(String taskType) {
        List<TaskDTO> byhashNotNull = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(taskType);
        if (!CollectionUtils.isEmpty(byhashNotNull)){
            byhashNotNull.stream().forEach(s->{
                this.delete(s);
            });
        }

    }

    private void deleteTaskFromScheduler(Task task) {
        Objects.requireNonNull(task);

        String jobName = task.getId() + "";
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.deleteJob(new JobKey(jobName, TASK_GROUP));
            log.debug("delete job={} from scheduler success", jobName);
        } catch (SchedulerException e) {
            log.warn("delete job id={} from scheduler failure", jobName);
        }
    }


    private Long timeAdjust(Long time) {
        if (Objects.isNull(time)) {
            return Long.MAX_VALUE;
        }
        String timer = time.toString();
        if (timer.endsWith("0")) {
            Long oneDay = 24L * 60 * 60 * 1000;
            //调整时间，增加23小时59分59秒999ss
            return time + oneDay - 1;
        }
        return time;
    }


    @Override
    public void runRightNow(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        Scheduler scheduler = schedulerFactoryBean.getScheduler();


        log.info("run task id={}, name={} right now", task.getId(), task.getTaskName());
        String jobName = task.getId() + "";

        try {
            JobKey key = new JobKey(jobName, TASK_GROUP);
            if (Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {

                JobDetail jobdetail = scheduler.getJobDetail(key);
                if (jobdetail == null) {
                    addTaskToScheduler(task);
                }
            }
            scheduler.triggerJob(key);
            //问题单202005261474，一次执行的任务点击立即执行后不再重复执行
            if (Objects.equals(task.getCycleType(),Task.CYCLE_TYPE_ONCE)) {
                scheduler.deleteJob(key);
            }
        } catch (SchedulerException e) {
            log.error("trigger job={} failure", jobName, e);
//            throw new AppException(ErrorCodes.Failed_to_trigger_task, task.getTaskName());
        }

    }

    @Override
    public void deleteByResourceId(List<String> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)){
            log.info("[TASK] delete task by resource fail because resource is null");
            return;
        }
        List<TaskDTO> allTaskDTO = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_STRATEGY_ISSUE);
        List<TaskDTO> allWarnTaskDTO = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_WARN_STRATEGY_ISSUE);
        allTaskDTO.addAll(allWarnTaskDTO);
        Map<String, List<TaskDTO>> resourceIdTaskMap = allTaskDTO.parallelStream().collect(Collectors.groupingBy(TaskDTO::getResourceId));
        List<TaskDTO> deleteTask = resourceIds.stream().map(d -> resourceIdTaskMap.get(d)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        deleteTask.stream().forEach(d->this.delete(d));
        log.info("[TASK] delete task by resource success");
    }


}
