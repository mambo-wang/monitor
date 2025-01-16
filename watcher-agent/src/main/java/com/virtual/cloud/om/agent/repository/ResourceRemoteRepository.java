package com.virtual.cloud.om.agent.repository;

import com.virtual.cloud.om.agent.entity.ResourceRemote;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author:XK
 * @Date:2022/9/2 11:27
 */
public interface ResourceRemoteRepository extends MongoRepository<ResourceRemote, ObjectId> {

    ResourceRemote findByResourceId(String resourceId);
}
