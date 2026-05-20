package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author:XK
 * @Date:2022/5/13 9:29
 */
@Data
@Accessors(chain = true)
public class TaskDTO implements Serializable {

    private static final long serialVersionUID = -7941942283954469788L;
    @Schema(description="任务id")
    private String id;

    /** 任务名 */
    @Schema(description="任务名")
    private String taskName;

    /** 任务描述 */
    @Schema(description="任务描述")
    private String description;

    /** 任务周期，1:一次；2:每月；3:每周；4:每天 */
    @Schema(description="任务周期，1:一次；2:每月；3:每周；4:每天")
    private String cycleType;

    /** 每个月的第TASK_DAY(1~31)天,或每个周的第TASK_DAY(1~7)天，如果按每天或只执行一次则该属性无效（-1） */
    @Schema(description="每个月的第TASK_DAY(1~31)天,或每个周的第TASK_DAY(1~7)天")
    private Integer cycleDay;

    /** 每天的TASK_TIME(00:00:00~23:59:59)时间 */
    @Schema(description="每天的TASK_TIME(00:00:00~23:59:59)时间 ")
    private String cycleTime;

    /** 任务类型 */
    @Schema(description="任务类型")
    private String taskType;

    /** 任务创建时间 */
    @Schema(description="任务创建时间")
    private String createdTime;

    /** 任务有效起始时间 */
    @Schema(description="任务有效起始时间")
    private Long availableStartTime;

    /** 任务有效终止时间 */
    @Schema(description="任务有效终止时间")
    private Long availableEndTime;

    private String hash;

    private Object data;

    private String resourceId;


}
