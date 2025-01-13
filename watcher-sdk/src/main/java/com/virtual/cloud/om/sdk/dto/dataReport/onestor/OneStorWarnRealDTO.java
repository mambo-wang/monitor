package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel(value = "实时告警")
public class OneStorWarnRealDTO {

    //告警模块
    private static final String CLUSTER = "集群";
    private static final String DEVICE = "设备";
    private static final String OS = "系统";
    private static final String NET = "网络";
    private static final String NAS = "NAS服务器";

    //告警级别
    private static final Integer CRITICAL_LEVEL = 1;
    private static final Integer MAJOR_LEVEL = 2;
    private static final Integer MINOR_LEVEL = 3;
    private static final Integer WARNING_LEVEL = 4;
    private static final Integer SELF_DEFINE_LEVEL = 5;

    //告警类型
    private static final Integer CLUSTER_TYPE = 3;
    private static final Integer DEVICE_TYPE = 701;
    private static final Integer NET_TYPE = 702;
    private static final Integer OS_TYPE = 703;
    private static final Integer NAS_TYPE = 704;

    @ApiModelProperty(value="alarm_service_log")
    private String alarm_service_log;
    @ApiModelProperty(value="recovery_time")
    private String recovery_time;
    @ApiModelProperty(value="alarm_level")
    private String alarm_level;
    @ApiModelProperty(value="class_id")
    private String class_id;
    @ApiModelProperty(value="alarm_module")
    private String alarm_module;
    @ApiModelProperty(value="alarm_time")
    private String alarm_time;
    @ApiModelProperty(value="alarm_content")
    private String alarm_content;
    @ApiModelProperty(value="alarm_status")
    private String alarm_status;
    @ApiModelProperty(value="confirm_time")
    private String confirm_time;
    @ApiModelProperty(value="index_id")
    private Long index_id;
    @ApiModelProperty(value="alarm_source")
    private String alarm_source;
    @ApiModelProperty(value="nodepool_name")
    private String nodepool_name;
    @ApiModelProperty(value = "告警级别")
    private Integer level;
    @ApiModelProperty(value = "告警类型")
    private Integer type;
    @ApiModelProperty(value = "告警对象类别")
    private Integer objectType;

    public Integer getLevel() {
        if (level == null&& StringUtils.isNotBlank(alarm_level)){
            if (alarm_level.equals("critical")){
                return CRITICAL_LEVEL;
            }
            if (alarm_level.equals("major")){
                return MAJOR_LEVEL;
            }
            if (alarm_level.equals("minor")){
                return MINOR_LEVEL;
            }
            if (alarm_level.equals("warning")){
                return WARNING_LEVEL;
            }
            if (alarm_level.equals("self_define")){
                return SELF_DEFINE_LEVEL;
            }
        }
        return level;
    }

    public Integer getType() {
        if (type == null&& StringUtils.isNotBlank(alarm_module)){
            //集群资源告警
            if (alarm_module.equals(CLUSTER)){
                return CLUSTER_TYPE;
            }
            //设备告警
            if (alarm_module.equals(DEVICE)){
                return DEVICE_TYPE;
            }
            //网络告警
            if (alarm_module.equals(NET)){
                return NET_TYPE;
            }
            //系统告警
            if (alarm_module.equals(OS)){
                return OS_TYPE;
            }
            //NAS服务器告警
            if (alarm_module.equals(NAS)){
                return NAS_TYPE;
            }
        }
        return type;
    }

    public Integer getObjectType() {
        if (objectType == null&& StringUtils.isNotBlank(alarm_module)){
            //集群资源告警
            if (alarm_module.equals(CLUSTER)){
                return WarnConstant.ObjectType.CLUSTER_OBJECTTYPE;
            }
            //设备告警
            if (alarm_module.equals(DEVICE)){
                return WarnConstant.ObjectType.DEVICE_OBJECTTYPE;
            }
            //网络告警
            if (alarm_module.equals(NET)){
                return WarnConstant.ObjectType.NET_OBJECTTYPE;
            }
            //系统告警
            if (alarm_module.equals(OS)){
                return WarnConstant.ObjectType.OS_OBJECTTYPE;
            }
            //NAS服务器告警
            if (alarm_module.equals(NAS)){
                return WarnConstant.ObjectType.NAS_OBJECTTYPE;
            }
        }
        return objectType;
    }
}
