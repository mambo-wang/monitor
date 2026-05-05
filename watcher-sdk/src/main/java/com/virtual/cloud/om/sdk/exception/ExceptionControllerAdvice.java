package com.virtual.cloud.om.sdk.exception;

import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.StateResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice
@ResponseBody
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Resource(name = "errorCodeMessageSource")
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RpcResult<Object> rpcResult = RpcResult.fail(ErrorCodes.PARAMETER_VALUE_IS_NULL, ex.getBindingResult().getFieldError().getDefaultMessage());
        return new ResponseEntity<>(rpcResult, HttpStatus.OK);
    }

    /**
     * 自定义的AppException，直接取出错误码和错误信息返回。
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(AppException.class)
    public StateResult handleException(AppException ae) {
        log.warn("[error-occur] AppException occur, msg:{}, e:{}", ae.getMessage(), ae);
        return RpcResult.fail(ae.getErrorCode(), ae.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalStateException.class)
    public StateResult handleException(IllegalStateException e) {
        log.warn("[error-occur] IllegalStateException occur, msg:{}, e:{}", e.getMessage(), e);
        return buildParamCheckErrorResult(e.getMessage(), ErrorCodes.PARAMETER_VALUE_ERROR);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NullPointerException.class)
    public StateResult handleException(NullPointerException e) {
        log.warn("[error-occur] NullPointerException occur, msg:{}, e:{}", e.getMessage(), e);
        return buildParamCheckErrorResult(e.getMessage(), ErrorCodes.PARAMETER_VALUE_IS_NULL);
    }

    private StateResult buildParamCheckErrorResult(String message, int parameterValueIsNull) {
        StateResult stateResult = new StateResult();
        stateResult.setState(StateResult.FAILURE);
        //如果Message为空，考虑可能是系统业务抛出的NPE或者State异常，对外仍然体现未知错误。
        if (StringUtils.isBlank(message)) {
            stateResult.setState(StateResult.FAILURE);
            stateResult.setErrorCode(ErrorCodes.UNKNOWN_ERROR);
        } else {
            stateResult.setState(StateResult.FAILURE);
            stateResult.setErrorCode(parameterValueIsNull);
            stateResult.setFailureMessage(message);
        }
        return stateResult;
    }

    /**
     * 处理其他未知的异常信息，统一返回错误码16，内容：未知异常。
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public StateResult handleException(Exception e, HttpServletRequest request) {
        log.warn("[error-occur] DataAccessException occur, msg:{}, e:{}", e.getMessage(), e);
        return RpcResult.fail(ErrorCodes.UNKNOWN_ERROR, buildFailureMessage(ErrorCodes.UNKNOWN_ERROR, request));
    }


    private String buildFailureMessage(Integer errorCode, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage("errorCode." + errorCode, null, locale);
    }

}
