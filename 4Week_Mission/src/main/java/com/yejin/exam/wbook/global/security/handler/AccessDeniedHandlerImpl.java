package com.yejin.exam.wbook.global.security.handler;

import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private String errorPage;

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("[accessDeniedHandler] AccessDeniedException", accessDeniedException);
//        log.error("[accessDeniedHandler] error : "+accessDeniedException.getMessage());
//        response.sendRedirect(Rq.urlWithErrorMsg(errorPage,accessDeniedException.getMessage()));
//        response.sendRedirect((Util.spring.responseEntityOf(ResultResponse.failOf("Auth_FAILED","접근 권한이 없습니다.",null))).toString());
//        response.sendRedirect(Rq.redirectWithErrorMsg("/denied",ResultResponse.failOf("Auth_FAILED","접근 권한이 없습니다.",null)));
        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    }
}