package com.virtual.cloud.om.agent.repository;

import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 任务仓库 - MySQL 单机版
 * MongoDB 版本已禁用，使用简化实现
 * @author:XK
 * @Date:2022/4/27 14:16
 */
@Slf4j
@Repository("taskRepository")
public class TaskRepositoryImpl implements TaskRepository {

    @Override
    public void save(Task task) {
        log.warn("[TaskRepository] 任务保存功能已禁用");
    }

    @Override
    public List<Task> findAll() {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public List<Task> findTaskResourceIdNotNull() {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public void delete(Task task) {
        log.warn("[TaskRepository] 任务删除功能已禁用");
    }

    @Override
    public void deleteTaskResourceIdNotNull() {
        log.warn("[TaskRepository] 任务删除功能已禁用");
    }

    @Override
    public Task findById(String id) {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return new Task();
    }

    @Override
    public Task findByName(String taskName) {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return null;
    }

    @Override
    public List<TaskDTO> findByhashNotNullAndTaskTypeIsStrategyIssue(String taskType) {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public List<TaskDTO> findByResourceId(String resourceId) {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public List<TaskDTO> findAllTaskDTO() {
        log.debug("[TaskRepository] 任务查询功能已禁用");
        return Collections.emptyList();
    }
}
