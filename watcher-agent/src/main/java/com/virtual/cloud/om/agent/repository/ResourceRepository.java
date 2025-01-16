package com.virtual.cloud.om.agent.repository;

import com.virtual.cloud.om.agent.entity.ResourceEntity;
import com.virtual.cloud.om.agent.entity.ResourceRemote;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author:XK
 * @Date:2022/9/2 14:08
 */
public interface ResourceRepository extends MongoRepository<ResourceEntity, ObjectId> {

    ResourceEntity findById(String id);
}
