package com.virtual.cloud.om.sdk.api;

import java.util.Optional;

/**
 * @Author: w22798
 * @Date: 2022/5/10 19:29
 */
public interface ParameterApi {
    /**
     * 获取参数
     * @param type 类型
     * @param name 名称
     * @return
     */
    Optional<String> queryParameterByTypeAndName(String type, String name);

    /**
     * 添加或者修改参数
     * @param value 值
     * @param type 类型
     * @param name 名称
     */
    void editParamByTypeAndName(String value, String type, String name);

    /**
     * 删除参数
     * @param type 类型
     * @param name 名称
     */
    void deleteParameters(String type, String name);
}
