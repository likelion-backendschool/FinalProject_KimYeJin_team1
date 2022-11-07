package com.yejin.exam.wbook.global.security.jwt;

import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        log.error("[authentication entrypoint] error : "+authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage());
//        response.sendRedirect(Rq.redirectWithErrorMsg("/member/login", ResultResponse.failOf("Auth_FAILED","접근 권한이 없습니다.",null)));
//        response.sendRedirect(Rq.redirectWithErrorMsg("/denied",ResultResponse.failOf("Auth_FAILED","접근 권한이 없습니다.",null)));
//        response.sendRedirect(Util.spring.responseEntityOf(ResultResponse.failOf("Auth_FAILED","접근 권한이 없습니다.",null)).toString());
    }
}