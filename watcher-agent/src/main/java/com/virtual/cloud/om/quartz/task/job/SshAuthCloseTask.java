package com.virtual.cloud.om.quartz.task.job;

import cn.hutool.core.collection.CollectionUtil;

import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.entity.ResourceEntity;
import com.virtual.cloud.om.entity.ResourceRemote;
import com.virtual.cloud.om.entity.ResourceRemoteOamServer;
import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.repository.ResourceRemoteOamServerRepository;
import com.virtual.cloud.om.repository.ResourceRemoteRepository;
import com.virtual.cloud.om.repository.ResourceRepository;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.service.resourceRemote.ResourceRemoteService;
import com.virtual.cloud.om.service.ssh.ResourceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.List;

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
