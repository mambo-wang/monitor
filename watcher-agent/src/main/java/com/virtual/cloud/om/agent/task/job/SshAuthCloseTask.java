package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.agent.entity.ResourceEntity;
import com.virtual.cloud.om.agent.entity.ResourceRemote;
import com.virtual.cloud.om.agent.entity.ResourceRemoteOamServer;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.ResourceRemoteOamServerRepository;
import com.virtual.cloud.om.agent.repository.ResourceRemoteRepository;
import com.virtual.cloud.om.agent.repository.ResourceRepository;
import com.virtual.cloud.om.agent.service.resourceRemote.ResourceRemoteService;
import com.virtual.cloud.om.agent.service.ssh.ResourceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

/**
 * @author:XK
 * @Date:2022/9/8 15:02
 */
@Slf4j
@DisallowConcurrentExecution
public class SshAuthCloseTask extends AbstractJob{

    @Resource
    private ResourceRepository resourceRepository;
    @Resource
    private ResourceRemoteService resourceRemoteService;
    @Resource
    private ResourceRemoteOamServerRepository resourceRemoteOamServerRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private ResourceRemoteRepository resourceRemoteRepository;
    @Resource
    private ResourceAuthService resourceAuthService;
    @Resource
    private TaskMgrApi taskMgrApi;
    @Override
    void doJob(Task task) {
        ResourceRemoteDTO taskData = (ResourceRemoteDTO) task.getData();
        resourceRemoteService.closeSsh(taskData);
        taskMgrApi.delete(task.getId());
    }
}
