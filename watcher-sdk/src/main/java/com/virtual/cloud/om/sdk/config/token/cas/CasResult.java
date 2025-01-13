package com.virtual.cloud.om.sdk.config.token.cas;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * uis接口返回体
 *
 * @author zkf9688
 */
@Data
@ToString
public class CasResult implements Serializable {

    private static final long serialVersionUID = 4395625710283753371L;

    public static final Integer UIS_API_SUCCESS = 0;

    public static final Integer UIS_API_FAILURE = 1;

    //0成功   1失败
    private Integer state;

    //错误码
    private Integer errorCode;

    //成功信息
    private String successMessage;

    //失败信息
    private String failureMessage;

    //数据
    private String data;
}
