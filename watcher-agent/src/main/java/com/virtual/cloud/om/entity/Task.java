package com.virtual.cloud.om.entity;

import com.virtual.cloud.om.sdk.dto.TaskDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "Task")//相当于数据库里的表名
public class Task implements Serializable {
    private static final long serialVersionUID = -5081419830740368061L;

    /** 状态，0:等待执行；1:执行中；2:完成 */
    public static final int STATE_WAIT = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISH = 2;

    /** 任务类型： 1:数据同步 */
    public static final int TYPE_SYNC = 1;
    public static final int TYPE_CHECK_CONNECT = 21;
    public static final int TYPE_CHECK_HA_STATUS = 22;

    /** 任务类型： 教室上下课消息推送 */
    public static final int TYPE_MESSAGE_PUSH = 23;

    /** 任务类型： 同步课程分发数据 */
    public static final int TYPE_CHECK_COURSE_SOFTWARE = 31;

    /** 运行一次 */
    public static final String CYCLE_TYPE_ONCE = "once";
    /** 每月 */
    public static final String CYCLE_TYPE_EVERYMONTH = "everyMonth";
    /** 每周 */
    public static final String CYCLE_TYPE_EVERYWEEK = "everyWeek";
    /** 每天 */
    public static final String CYCLE_TYPE_EVERYDAY = "everyDay";
    public static final String CYCLE_TYPE_HOUR = "hour";
    public static final String CYCLE_TYPE_MINUTES = "minute";
    public static final String CYCLE_TYPE_SECOND = "second";
    /** 任务执行目标类型  1：策略下发生成定时任务*/
//    public static final int TASK_TYPE_LOG = 1;
    public static final String TASK_STRATEGY_ISSUE = "strategy_issue";
    /** 任务执行目标类型  1：告警策略下发生成定时任务*/
//    public static final int TASK_TYPE_LOG = 1;
    public static final String TASK_WARN_STRATEGY_ISSUE = "warn_strategy_issue";
    /** 任务执行目标类型  1：策略拉取*/
    public static final String TASK_STRATEGY_PULL = "strategy_pull";

    /** 任务执行目标类型  1：告警策略拉取*/
    public static final String TASK_WARN_PULL = "warn_strategy_pull";

    /** 任务类型：健康检查 */
    public static final String TASK_HEALTH_CHECK = "health_check";

    /** 任务类型：Filebeat检查 */
    public static final String TASK_FILEBEAT_CHECK = "filebeat_check";

    /** 任务执行目标类型  1：采集端告警定时任务*/
    public static final String TASK_WATCHER_WARN = "watcher_warn";
    public static final String TASK_SSH_CLOSE = "ssh_close";

//    /** 任务执行目标类型  2：Uis采集类型 */
//    public static final int TASK_TYPE_UIS = 2;
//
//    /** 任务执行目标类型  3：cas采集类 */
//    public static final int TASK_TYPE_CAS = 3;


    /** 任务执行目标类型  1：cas采集类型 */
    public static final int OBJECT_TYPE_VDI = 1;

    /** 任务执行目标类型  2：Uis采集类型 */
    public static final int OBJECT_TYPE_UIS = 2;

    /** 任务执行目标类型  3：cas采集类 */
    public static final int OBJECT_TYPE_CAS = 3;

//    /** 任务执行目标类型  4：消息 （前台不展示，内部使用）*/
//    public static final int OBJECT_TYPE_MESSAGE = 4;


    @ApiModelProperty(value="任务id")
    private String id;

    /** 任务名 */
    @ApiModelProperty(value="任务名")
    private String taskName;

    /** 任务描述 */
    @ApiModelProperty(value="任务描述")
    private String description;

    /** 任务周期，1:一次；2:每月；3:每周；4:每天 */
    @ApiModelProperty(value="任务周期，1:一次；2:每月；3:每周；4:每天")
    private String cycleType;

    /** 每个月的第TASK_DAY(1~31)天,或每个周的第TASK_DAY(1~7)天，如果按每天或只执行一次则该属性无效（-1） */
    @ApiModelProperty(value="每个月的第TASK_DAY(1~31)天,或每个周的第TASK_DAY(1~7)天")
    private Integer cycleDay;

    /** 每天的TASK_TIME(00:00:00~23:59:59)时间 */
    @ApiModelProperty(value="每天的TASK_TIME(00:00:00~23:59:59)时间 ")
    private String cycleTime;

    /** 任务类型 */
    @ApiModelProperty(value="任务类型")
    private String taskType;

    /** 任务创建时间 */
    @ApiModelProperty(value="任务创建时间")
    private String createdTime;

    /** 任务有效起始时间 */
    @ApiModelProperty(value="任务有效起始时间")
    private Long availableStartTime;

    /** 任务有效终止时间 */
    @ApiModelProperty(value="任务有效终止时间")
    private Long availableEndTime;

    /** 策略的hash */
    @ApiModelProperty(value="策略的hash")
    private String hash;
    @ApiModelProperty(value="传递数据")
    private Object data;

    private String resourceId;

    public TaskDTO convertToDTO(){
        TaskDTO taskDTO =new TaskDTO();
        BeanUtils.copyProperties(this,taskDTO);
        return taskDTO;
    }

}

