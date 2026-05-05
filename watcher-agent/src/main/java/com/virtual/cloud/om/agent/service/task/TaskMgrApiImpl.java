package com.virtual.cloud.om.agent.service.task;

import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 任务管理服务 - MySQL 单机版
 * Quartz 版本已禁用，使用简化的数据库持久化
 */
@Slf4j
@Service("taskMgrApi")
public class TaskMgrApiImpl implements TaskMgrApi {

    @Autowired
    private TaskRepository taskRepository;

    private final String TASK_NAME = "{0}-{1}-{2}-classMsgTask";

    /**
     * 任务默认只在主节点执行
     */
    @PostConstruct
    public void initTaskScheduler() {
        log.info("initTaskScheduler disabled in MySQL standalone mode");
    }

    public Task convertToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        BeanUtils.copyProperties(taskDTO, task);
        return task;
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);
        return taskDTO;
    }

    public Task isTaskNameExist(String taskName) {
        return taskRepository.findByName(taskName);
    }

    public void addTaskToScheduler(Task task) {
        log.info("[TASK] addTaskToScheduler disabled in MySQL standalone mode, task: {}", task.getId());
    }

    @Override
    public TaskDTO queryById(Long id) {
        return null;
    }

    @Override
    public TaskDTO queryByName(String name) {
        Task byName = taskRepository.findByName(name);
        if (Objects.nonNull(byName)) {
            return this.convertToDTO(byName);
        }
        return null;
    }

    @Override
    public void addTask(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        addTaskToDB(task);
        log.info("[TASK] addTask success: {}", task.getTaskName());
    }

    @Transactional
    public void addTaskToDB(Task task) {
        Task taskNameExist = isTaskNameExist(task.getTaskName());
        if (Objects.nonNull(taskNameExist)) {
            TaskDTO taskDTO = this.convertToDTO(taskNameExist);
            this.delete(taskDTO);
        }
        taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        updateTaskToDB(task);
        log.info("[TASK] updateTask success: {}", task.getTaskName());
    }

    @Transactional
    public void updateTaskToDB(Task task) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(task.getId());
        log.info("update task id={}, name={}", task.getId(), task.getTaskName());
        taskRepository.save(task);
    }

    private void updateTaskToScheduler(Task task) {
        log.info("[TASK] updateTaskToScheduler disabled in MySQL standalone mode");
    }

    @Override
    public void delete(TaskDTO taskDTO) {
        Task task = this.convertToEntity(taskDTO);
        log.info("delete task id={}, name={}", task.getId(), task.getTaskName());
        taskRepository.delete(task);
    }

    @Override
    public void delete(String taskId) {
        Task task = taskRepository.findById(taskId);
        if (task != null) {
            log.info("delete task id={}, name={}", task.getId(), task.getTaskName());
            taskRepository.delete(task);
        }
    }

    @Override
    public void deleteAll() {
        log.info("deleteAll tasks disabled in MySQL standalone mode");
    }

    @Override
    public void deleteAllStrategyTask(String taskType) {
        List<TaskDTO> byhashNotNull = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(taskType);
        if (!CollectionUtils.isEmpty(byhashNotNull)) {
            byhashNotNull.forEach(this::delete);
        }
    }

    private void deleteTaskFromScheduler(Task task) {
        log.info("[TASK] deleteTaskFromScheduler disabled in MySQL standalone mode");
    }

    @Override
    public void runRightNow(TaskDTO taskDTO) {
        log.info("[TASK] runRightNow disabled in MySQL standalone mode");
    }

    @Override
    public void deleteByResourceId(List<String> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            log.info("[TASK] delete task by resource fail because resource is null");
            return;
        }
        List<TaskDTO> allTaskDTO = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_STRATEGY_ISSUE);
        List<TaskDTO> allWarnTaskDTO = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_WARN_STRATEGY_ISSUE);
        allTaskDTO.addAll(allWarnTaskDTO);
        for (TaskDTO dto : allTaskDTO) {
            if (resourceIds.contains(dto.getResourceId())) {
                this.delete(dto);
            }
        }
        log.info("[TASK] delete task by resource success");
    }
}
