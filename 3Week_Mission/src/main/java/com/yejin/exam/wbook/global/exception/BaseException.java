package com.yejin.exam.wbook.global.exception;

import java.util.ArrayList;
import java.util.List;

import com.yejin.exam.wbook.global.error.ErrorCode;
import com.yejin.exam.wbook.global.error.ErrorResponse;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private ErrorCode errorCode;
    private List<ErrorResponse.FieldError> errors = new ArrayList<>();

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode.getMessage());
        this.errors = errors;
        this.errorCode = errorCode;
    }
}