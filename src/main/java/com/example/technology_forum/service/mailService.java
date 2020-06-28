package com.example.technology_forum.service;

public interface mailService {
    /*收件人，邮箱主题，邮箱验证码*/
    boolean sendMail(String to, String subject, String VerifyCode);
}
