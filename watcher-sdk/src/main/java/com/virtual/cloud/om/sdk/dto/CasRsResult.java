package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "rsResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasRsResult<D> extends StateResult {


    private static final long serialVersionUID = 1L;


    private D data = null;


    public CasRsResult() {
    }

    public static <T> CasRsResult<T> success() {
        return new CasRsResult<>("success");
    }

    public static <T> CasRsResult<T> success(T t) {
        return new CasRsResult<>(t);
    }

    public static <T> CasRsResult<T> success(T t, String message) {
        return new CasRsResult<>(t, message);
    }

    public static <T> CasRsResult<T> success(String message) {
        return new CasRsResult<>(message);
    }

    public static <T> CasRsResult<T> fail(int code, String message) {
        return new CasRsResult<>(message, code, FAILURE);
    }

    public static <T> CasRsResult<T> fail(String message) {
        return new CasRsResult<>(message, StateResult.FAILURE, FAILURE);
    }

    public static <T> CasRsResult<T> partialSuccess(int code, String message) {
        return new CasRsResult<>(message, code, PARTIAL_SUCCESS);
    }

    public static <T> CasRsResult<T> error(int code, String message) {
        return new CasRsResult<>(message, code, ERROR);
    }


    public CasRsResult(String successMessage) {
        setState(SUCCESS);
        setSuccessMessage(successMessage);
    }


    public CasRsResult(D data) {
        setState(SUCCESS);
        this.data = data;
    }


    public CasRsResult(D data, String successMessage) {
        setState(SUCCESS);
        this.data = data;
        setSuccessMessage(successMessage);
    }

    public CasRsResult(String failureMessage, int errorCode) {
        setState(FAILURE);
        setErrorCode(errorCode);
        setFailureMessage(failureMessage);
    }

    public CasRsResult(String failureMessage, int errorCode, int state) {
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
