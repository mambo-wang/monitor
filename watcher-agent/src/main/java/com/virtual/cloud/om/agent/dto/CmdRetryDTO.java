package com.virtual.cloud.om.agent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/11/3 11:54
 */

/**
 * 远程命令行连接失败次数TO
 */
@Data
public class CmdRetryDTO implements Serializable {
    private static final long serialVersionUID = -6635747256261646132L;
    /**
     * 错误次数
     */
    private Integer frequency;
    /**
     * 等待时间
     */
    private Long time;
}
