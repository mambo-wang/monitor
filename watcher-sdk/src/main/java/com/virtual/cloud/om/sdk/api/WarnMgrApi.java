package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.WarnDTO;


public interface WarnMgrApi {
    /**
     * 获取告警信息
     * @param resourceId 资源id
     * @param type 告警类型
     * @return
     */
    WarnDTO queryWarnByIdAndType(String resourceId, String type);

    /**
     * 添加或者修改告警信息
     * @param resourceId 资源id
     * @param eventTime 最新告警时间
     */
    void editWarnByResourceIdAndType(String resourceId, Long eventTime,String type,Long reportTime);

    /**
     * 添加或者修改上报时间
     * @param resourceId 资源id
     */
    void editWarnByResourceId(String resourceId);

}
