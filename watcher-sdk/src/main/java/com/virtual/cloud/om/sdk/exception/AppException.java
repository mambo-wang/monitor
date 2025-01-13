package com.virtual.cloud.om.sdk.exception;

import com.virtual.cloud.om.sdk.utils.StringManager;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by JemmyZhang on 2018/2/26
 */
public class AppException extends RuntimeException implements Serializable {

    private static StringManager sm = StringManager.getManagerWithBundleName("messages.ErrorCode");

    private static final long serialVersionUID = 2860991341559790081L;

    private Integer errorCode;

    private Object[] data;

    public AppException(Integer errorCode) {
        super(buildErrorMessage(errorCode));
        this.errorCode = errorCode;
    }

    public AppException(Integer errorCode, Object... data) {
        super(buildErrorMessage(errorCode, data));
        this.errorCode = errorCode;
        this.data = data;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return Objects.isNull(message) ? getErrorMessage() : message;
    }

    public Object[] getData(){
        return this.data;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage(Object... data) {
        String msg = super.getMessage();
        //如果有主动设置错误信息，以主动设置的错误信息为准，不使用ErrorCode获取信息
        if (StringUtils.isNotEmpty(msg)) {
            return msg;
        }
        return buildErrorMessage(errorCode, data);
    }

    private static String buildErrorMessage(Integer errorCode, Object... data) {
        if (data != null) {
            return sm.getString("errorCode." + errorCode, data);
        } else {
            return sm.getString("errorCode." + errorCode);
        }
    }
}
