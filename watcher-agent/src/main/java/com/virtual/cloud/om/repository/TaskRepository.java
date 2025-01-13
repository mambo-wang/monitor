package com.virtual.cloud.om.repository;

import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.sdk.dto.TaskDTO;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/4/27 14:08
 */
public interface TaskRepository {

    void save(Task task);

    List<Task> findAll();

    List<Task> findTaskResourceIdNotNull();

    void  delete(Task task);

    void  deleteTaskResourceIdNotNull();

    Task findById(String id);

    Task findByName(String taskName);

    List<TaskDTO> findByhashNotNullAndTaskTypeIsStrategyIssue(String taskType);

    List<TaskDTO> findByResourceId(String resourceId);

    List<TaskDTO> findAllTaskDTO();
}
