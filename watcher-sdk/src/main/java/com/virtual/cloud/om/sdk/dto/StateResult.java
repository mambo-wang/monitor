package com.virtual.cloud.om.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema
public class StateResult implements Serializable {


    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;

    public static final int FAILURE = 1;

    public static final int PARTIAL_SUCCESS = 2;

    public static final int ERROR = 3;

    @Schema(description = "本次请求状态", example = "0")
    private int state = 0;

    @Schema(description = "错误码", example = "0")
    private int errorCode = 0;

    @Schema(description = "操作成功消息,仅成功时返回", example = "操作成功")
    private String successMessage = null;

    @Schema(description = "操作失败消息，仅失败时返回", example = "操作失败")
    private String failureMessage = null;

    // ------------------------------------------------------------------- ���ʷ���

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @JsonProperty("success")
    public boolean isSuccess() {
        return state == SUCCESS;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
}
