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
 * 密码策略表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("pwd_strategy")
public class PwdStrategy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 密码最小长度
     */
    private Integer minLength;

    /**
     * 密码复杂度: 1-简单 2-中等 3-复杂 4-最强
     */
    private Integer pwdComplex;

    /**
     * 密码有效期(天)
     */
    private Integer pwdLifeTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
