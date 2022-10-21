package com.yejin.exam.wbook.global.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String message = "Invaild Username or Password";
        log.error(message);
        if (exception instanceof BadCredentialsException) {
            message = "Invaild Username or Password";
            log.error(message);
        } else if (exception instanceof InsufficientAuthenticationException) {
            message = "Invalid Secret Key";
            log.error(message);
        }

        setDefaultFailureUrl("/login?error=true");

        super.onAuthenticationFailure(request, response, exception);

    }
}