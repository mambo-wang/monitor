package com.virtual.cloud.om.repository;

import com.virtual.cloud.om.entity.Deploy;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: w22798
 * @Date: 2022/5/8 16:35
 */
@Repository
public interface DeployRepository extends MongoRepository<Deploy, ObjectId> {

    /** 根据IP查询 */
    Deploy findByIp(String ip);
}
