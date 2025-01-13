package com.virtual.cloud.om.sdk.dto;

public class RpcResult<D> extends StateResult {


    private static final long serialVersionUID = 1L;


    private D data = null;


    public RpcResult() {
    }

    public static <T> RpcResult<T> success() {
        return new RpcResult<>("success");
    }

    public static <T> RpcResult<T> success(T t) {
        return new RpcResult<>(t);
    }

    public static <T> RpcResult<T> success(T t, String message) {
        return new RpcResult<>(t, message);
    }

    public static <T> RpcResult<T> success(String message) {
        return new RpcResult<>(message);
    }

    public static <T> RpcResult<T> fail(int code, String message) {
        return new RpcResult<>(message, code, FAILURE);
    }

    public static <T> RpcResult<T> fail(String message) {
        return new RpcResult<>(message, StateResult.FAILURE, FAILURE);
    }

    public static <T> RpcResult<T> partialSuccess(int code, String message) {
        return new RpcResult<>(message, code, PARTIAL_SUCCESS);
    }

    public static <T> RpcResult<T> error(int code, String message) {
        return new RpcResult<>(message, code, ERROR);
    }


    public RpcResult(String successMessage) {
        setState(SUCCESS);
        setSuccessMessage(successMessage);
    }


    public RpcResult(D data) {
        setState(SUCCESS);
        this.data = data;
    }


    public RpcResult(D data, String successMessage) {
        setState(SUCCESS);
        this.data = data;
        setSuccessMessage(successMessage);
    }

    public RpcResult(String failureMessage, int errorCode) {
        setState(FAILURE);
        setErrorCode(errorCode);
        setFailureMessage(failureMessage);
    }

    public RpcResult(String failureMessage, int errorCode, int state) {
        setState(state);
        setErrorCode(errorCode);
        setFailureMessage(failureMessage);
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }


}
