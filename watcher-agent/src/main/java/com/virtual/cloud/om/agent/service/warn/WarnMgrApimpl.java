package com.virtual.cloud.om.agent.service.warn;

import com.virtual.cloud.om.agent.entity.Warn;
import com.virtual.cloud.om.sdk.dto.WarnDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WarnMgrApimpl implements WarnMgrApi {

    /**
     * 获取告警信息 - 已禁用（MongoDB已移除）
     * @param resourceId 资源id
     * @param type 告警类型
     * @return
     */
    public WarnDTO queryWarnByIdAndType(String resourceId, String type) {
        log.warn("[WarnMgr] 告警查询功能已禁用（MongoDB已移除）");
        return null;
    }

    /**
     * 添加或者修改告警信息 - 已禁用
     * @param resourceId 资源id
     * @param eventTime 最新告警时间
     */
    public void editWarnByResourceIdAndType(String resourceId, Long eventTime, String type, Long reportTime) {
        log.warn("[WarnMgr] 告警编辑功能已禁用（MongoDB已移除）");
    }

    @Override
    public void editWarnByResourceId(String resourceId) {
        log.warn("[WarnMgr] 告警编辑功能已禁用（MongoDB已移除）");
    }

    public Warn convertToEntity(WarnDTO warnDTO) {
        Warn dto = new Warn();
        BeanUtils.copyProperties(warnDTO, dto);
        return dto;
    }

    public WarnDTO convertToDTO(Warn warn) {
        WarnDTO warnDTO = new WarnDTO();
        BeanUtils.copyProperties(warn, warnDTO);
        return warnDTO;
    }
}
