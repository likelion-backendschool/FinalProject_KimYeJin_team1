package com.yejin.exam.wbook.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendMessage(String email, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kyj2212@gmail.com");
        message.setTo(email);
        message.setText(text);
        emailSender.send(message);
    }
}
