package com.virtual.cloud.om.agent.service.parameter;

import com.virtual.cloud.om.sdk.api.ParameterApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 参数服务 - MySQL 单机版
 * MongoDB 版本已禁用，使用简化实现
 * @Author: w22798
 * @Date: 2022/5/5 11:17
 */
@Service
@Slf4j
public class ParameterService implements ParameterApi {

    /**
     * 获取参数 - 已禁用
     * @param type 类型
     * @param name 名称
     * @return
     */
    @Override
    public Optional<String> queryParameterByTypeAndName(String type, String name) {
        log.debug("[ParameterService] 参数查询功能已禁用");
        return Optional.empty();
    }

    /**
     * 添加或者修改参数 - 已禁用
     * @param value 值
     * @param type 类型
     * @param name 名称
     */
    @Override
    public void editParamByTypeAndName(String value, String type, String name) {
        log.debug("[ParameterService] 参数编辑功能已禁用");
    }

    /**
     * 删除参数 - 已禁用
     * @param type 类型
     * @param name 名称
     */
    @Override
    public void deleteParameters(String type, String name) {
        log.debug("[ParameterService] 参数删除功能已禁用");
    }
}
