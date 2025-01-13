package com.virtual.cloud.om.repository;

import com.virtual.cloud.om.entity.ResourceRemote;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/9/2 11:27
 */
public interface ResourceRemoteRepository extends MongoRepository<ResourceRemote, ObjectId> {

    ResourceRemote findByResourceId(String resourceId);
}
