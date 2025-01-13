package com.virtual.cloud.om.sdk.dto.operate;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement(name = "taskMsg")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasRsTaskMsg {
    /** 消息ID */
    private Long msgId;

    /** 消息标题 */
    private String name;

    /** 任务执行对象ID */
    private Long targetId;

    /** 任务执行对象名称 */
    private String targetName;
    /** 任务描述。*/
    private String detail;

    /** 任务是否完成 */
    private Boolean completed;

    /** 执行结果（0-成功；1-失败；2-部分成功）。 */
    private Integer result;

    /** 进度 */
    private Integer progress;

    /** 失败信息 */
    private String failMsg;

    /** 事件类型。*/
    private int eventType;

    /**开始时间**/
    private Date start;

    /**结束时间**/
    private Date complete;

    private Boolean oldTask;
}
