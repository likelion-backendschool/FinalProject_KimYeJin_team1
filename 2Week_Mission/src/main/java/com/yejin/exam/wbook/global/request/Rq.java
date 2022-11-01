package com.yejin.exam.wbook.global.request;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@Slf4j
@RequestScope
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberContext memberContext;
    @Getter
    private final Member member;

    public Rq(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof MemberContext) {
            this.memberContext = (MemberContext) authentication.getPrincipal();
            this.member = memberContext.getMember();
        } else {
            this.memberContext = null;
            this.member = null;
        }
    }

    public String redirectToBackWithMsg(String msg) {
        String url = req.getHeader("Referer");

        return redirectWithMsg(url, msg);
    }

    public boolean hasAuthority(String authorityName) {
        if( memberContext == null ) return false;

        return memberContext.hasAuthority(authorityName);
    }

    public String historyBack(String msg) {
        req.setAttribute("alertMsg", msg);
        return "common/js";
    }

    public String historyBack(ResultResponse rsData) {
        return historyBack(rsData.getMessage());
    }

    public static String urlWithMsg(String url, ResultResponse rsData) {
        if (rsData.isFail()) {
            return urlWithErrorMsg(url, rsData.getMessage());
        }

        return urlWithMsg(url, rsData.getMessage());
    }

    public static String urlWithMsg(String url, String msg) {
        return Util.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    public static String urlWithErrorMsg(String url, String errorMsg) {
        return Util.url.modifyQueryParam(url, "errorMsg", msgWithTtl(errorMsg));
    }

    public static String redirectWithMsg(String url, ResultResponse rsData) {
        return "redirect:" + urlWithMsg(url, rsData);
    }

    public static String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }

    private static String msgWithTtl(String msg) {
        return Util.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

    public String redirectWithErrorMsg(String url, ResultResponse rsData) {
        url = Util.url.modifyQueryParam(url, "errorMsg", msgWithTtl(rsData.getMessage()));

        return "redirect:" + url;
    }

    public long getId() {
        if (isLogout()) {
            return 0;
        }
        return getMember().getId();
    }

    public boolean isLogout() {
        return member == null;
    }

    public boolean isLogined() {
        return isLogout() == false;
    }
}