package com.petsitter.pay.handler;

import com.petsitter.common.exception.BusinessException;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        return Result.error(ResultCode.PARAM_ERROR.getCode(), resolveFirstError(e.getBindingResult()));
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        return Result.error(ResultCode.PARAM_ERROR.getCode(), resolveFirstError(e.getBindingResult()));
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    private String resolveFirstError(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        return fieldError != null ? fieldError.getDefaultMessage() : ResultCode.PARAM_ERROR.getMessage();
    }
}
