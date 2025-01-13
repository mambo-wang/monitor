package com.virtual.cloud.om.quartz.task.job;


import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.SpringContextsUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.impl.JobDetailImpl;

import java.util.Objects;

@Slf4j
public abstract class AbstractJob implements Job {

    TaskMgrApi taskMgrApi;

    TaskRepository taskRepository;

    AbstractJob() {
        taskMgrApi = (TaskMgrApi) SpringContextsUtil.getBean("taskMgrApi");
        taskRepository = (TaskRepository) SpringContextsUtil.getBean("taskRepository");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDetailImpl detail = (JobDetailImpl) jobExecutionContext.getJobDetail();
        String id = detail.getName();
        Task task = taskRepository.findById(id);
        if (Objects.isNull(task)){
            throw new AppException(ErrorCodes.TASK_NOT_FOUND);
        }

        log.debug("[task] task:id={},name={} executed.", task.getId(), task.getTaskName());
        doJob(task);
    }

    abstract void doJob(Task task);
}
