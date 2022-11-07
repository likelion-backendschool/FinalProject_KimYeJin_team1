package com.yejin.exam.wbook.global.result;

import com.yejin.exam.wbook.util.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ResultResponse<T> {
    private String resultCode;
    private String message;
    private T data;

    public ResultResponse(String resultCode,String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultResponse<T> of(String resultCode,String message, T data) {
        return new ResultResponse<>(resultCode,message, data);
    }

    public static <T> ResultResponse<T> of(String resultCode, String message) {
        return new ResultResponse<>(resultCode,message,null);
    }

    public static <T> ResultResponse<T> successOf(String resultCode,String message,T data) {
        return of(resultCode, message, data);
    }

    public static <T> ResultResponse<T> failOf(String resultCode,String message,T data) {
        return of(resultCode, message, data);
    }

    public boolean isSuccess() {
        return resultCode.endsWith("OK");
    }

    public boolean isFail() {
        return isSuccess() == false;
    }

    public String addMessageToUrl(String url) {
        if ( isFail() ) {
            return Util.url.modifyQueryParam(url, "errorMsg", getMessage());
        }

        return Util.url.modifyQueryParam(url, "msg", getMessage());
    }
}