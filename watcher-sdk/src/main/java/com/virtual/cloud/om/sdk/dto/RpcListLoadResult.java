package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema
public class RpcListLoadResult<D> extends StateResult implements ListLoadResult<D> {


    private static final long serialVersionUID = 1L;

    @Schema(description = ("数据"))
    private List<D> data = null;

    protected RpcListLoadResult() {
    }


    public RpcListLoadResult(List<D> data) {
        setState(SUCCESS);
        this.data = data;
    }


    public RpcListLoadResult(List<D> data, String successMessage) {
        setState(SUCCESS);
        this.data = data;
        setSuccessMessage(successMessage);
    }

    public RpcListLoadResult(int errorCode, String failureMessage) {
        setState(FAILURE);
        setErrorCode(errorCode);
        setFailureMessage(failureMessage);
    }

    public static <T> RpcListLoadResult<T> success(List<T> t) {
        return new RpcListLoadResult<>(t);
    }

    public static <T> RpcListLoadResult<T> fail(int code, String message) {
        return new RpcListLoadResult<>(code, message);
    }

    @Override
    public List<D> getData() {
        return data;
    }

    public void setData(List<D> data) {
        this.data = data;
    }
}
