package com.yejin.exam.wbook.domain.home.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdmHomeController {
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public String showIndex() {
        return "redirect:/adm/home/main";
    }

    @GetMapping("/home/main")
    @PreAuthorize("hasRole('ADMIN')")
    public String showMain() {
        return "adm/home/main";
    }
}