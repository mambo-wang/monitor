package com.virtual.cloud.om.entity;

import com.virtual.cloud.om.sdk.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

/**
 * @Author: w22798
 * @Date: 2022/5/05 11:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Parameter")
public class Parameter implements Serializable {

    /** 序列化ID。 */
    private static final long serialVersionUID = 1L;

    /**系统基本参数type*/
    public static final String SYS_CONF = Constant.Parameter.SYS_CONF;

    /**虚IP*/
    public static final String NAME_VIP = Constant.Parameter.NAME_VIP;
    public static final String NAME_CLUSTER_STEP = Constant.Parameter.NAME_CLUSTER_STEP;
    public static final String NAME_CLUSTER_STATUS = Constant.Parameter.NAME_CLUSTER_STATUS;
    public static final String NAME_CLUSTER_RESULT = Constant.Parameter.NAME_CLUSTER_RESULT;

    /** 参数类型。 */
    private String type = null;

    /** 参数名称。 */
    private String name = null;

    /** 参数值。 */
    private String value = null;

}
