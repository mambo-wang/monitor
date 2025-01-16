package com.virtual.cloud.om.agent.repository;

import com.virtual.cloud.om.agent.entity.Deploy;
import com.virtual.cloud.om.agent.entity.ResourceRemoteOamServer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/9/2 9:33
 */
@Repository
public interface ResourceRemoteOamServerRepository extends MongoRepository<ResourceRemoteOamServer, ObjectId> {

    ResourceRemoteOamServer findByUserIdAndResourceId(String userId, String resourceId);

    List<ResourceRemoteOamServer> findByResourceId(String resourceId);
}
