package com.yejin.exam.wbook.global.error.controller;

import com.yejin.exam.wbook.global.result.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
public class ErrorController {

    @GetMapping("/denied")
    @ResponseBody
    public ResultResponse accessDenied(String errorMsg){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("[denied] auth username : "+ authentication.getName() + " authority : "+authentication.getAuthorities());
        log.debug("[denied] exception : "+ errorMsg);

        return ResultResponse.of("ACCESS_DENIED",errorMsg);
    }

}
