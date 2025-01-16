package com.virtual.cloud.om.agent.repository;

import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/4/27 14:16
 */
@Slf4j
@Repository("taskRepository")
public class TaskRepositoryImpl implements TaskRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Task task) {
        mongoTemplate.save(task);
        log.info("success save a task {}", task);
    }

    @Override
    public List<Task> findAll() {
        return mongoTemplate.findAll(Task.class);
    }

    @Override
    public List<Task> findTaskResourceIdNotNull() {
        Query query = Query.query(Criteria.where("resourceId").exists(true));
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        log.info("[findTaskResourceIdNotNull] tasks name is {}",tasks.stream().map(Task::getTaskName).collect(Collectors.toList()));
        return tasks;
    }

    @Override
    public void delete(Task task) {
        mongoTemplate.remove(task);
    }

    @Override
    public void deleteTaskResourceIdNotNull() {
        Query query = Query.query(Criteria.where("resourceId").exists(true));
        mongoTemplate.remove(query,Task.class);
    }

    @Override
    public Task findById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Task task = mongoTemplate.findOne(query, Task.class);
        if (Objects.nonNull(task)) {
            return task;
        }
        return new Task();
    }

    @Override
    public Task findByName(String taskName) {
        Query query = Query.query(Criteria.where("taskName").is(taskName));
        Task tasks = mongoTemplate.findOne(query, Task.class);
        return tasks;
    }

    @Override
    public List<TaskDTO> findByhashNotNullAndTaskTypeIsStrategyIssue(String taskType) {
        Query query = Query.query(Criteria.where("hash").exists(true).and("taskType").is(taskType));
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        if (!CollectionUtils.isEmpty(tasks)) {
            List<TaskDTO> taskDTOList = tasks.stream().map(t -> t.convertToDTO()).collect(Collectors.toList());
            return taskDTOList;
        }
        return Collections.emptyList();
    }

    @Override
    public List<TaskDTO> findByResourceId(String resourceId) {
        Query query = Query.query(Criteria.where("resourceId").is(resourceId));
        List<Task> taskList = mongoTemplate.find(query, Task.class);
        if (CollectionUtils.isEmpty(taskList)) {
            return Collections.emptyList();
        }
        List<TaskDTO> taskDTOS = taskList.stream().map(t -> t.convertToDTO()).collect(Collectors.toList());
        return taskDTOS;
    }

    @Override
    public List<TaskDTO> findAllTaskDTO() {
        List<Task> tasks = this.findAll();
        if (CollectionUtils.isEmpty(tasks)){
            return Collections.emptyList();
        }
        List<TaskDTO> taskDTOS = tasks.stream().map(t -> t.convertToDTO()).collect(Collectors.toList());
        return taskDTOS;
    }
}
