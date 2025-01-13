package com.virtual.cloud.om.sdk.api;




import com.virtual.cloud.om.sdk.dto.TaskDTO;

import java.util.List;

public interface TaskMgrApi {


    TaskDTO queryById(Long id);

    TaskDTO queryByName(String name);

    /**
     * 添加定时任务
     * @param taskDTO 定时任务
     * @return 定时任务id
     */
    void addTask(TaskDTO taskDTO);

    void update(TaskDTO taskDTO);

    void delete(TaskDTO taskDTO);

    void delete(String taskId);

    void deleteAll();

    void deleteAllStrategyTask(String taskType);



    void runRightNow(TaskDTO taskDTO);

    void deleteByResourceId(List<String> resourceIds);


//    Long addMessageTask(Long resourceId, Long classroomId, Long startTime, Long messageId);

}
