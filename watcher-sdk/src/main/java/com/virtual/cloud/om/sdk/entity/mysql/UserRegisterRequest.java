package com.virtual.cloud.om.sdk.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户注册申请表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user_register_request")
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 申请用户名
     */
    private String username;

    /**
     * 密码(SM4加密)
     */
    private String password;

    /**
     * 状态: pending-待审批/approved-已通过/rejected-已拒绝
     */
    private String status;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批人用户名
     */
    private String approver;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
