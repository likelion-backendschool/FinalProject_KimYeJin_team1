package com.yejin.exam.wbook.global.exception;

import com.yejin.exam.wbook.global.error.ErrorCode;

public class EntityAlreadyExistException extends BaseException{
    public EntityAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
