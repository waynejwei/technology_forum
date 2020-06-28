package com.example.technology_forum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class mailServiceImpl implements mailService{

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendMail(String to, String subject, String VerifyCode) {

        String context="尊敬的用户,您好:"+"\n"+"本次请求的邮件验证码为:"+VerifyCode+",本验证码五分钟内有效，请及时输入。（请勿泄露此验证码）\n " +
                "如非本人操作，请忽略该邮件。\n(这是一封自动发送的邮件，请不要直接回复）";
        SimpleMailMessage message = new SimpleMailMessage();
        /*收件人*/
        String from = "513512942@qq.com";
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(context);
        try {
            javaMailSender.send(message);
            log.info("邮箱验证码已送达");
            return true;
        }catch (Exception e){
            log.info("邮箱验证码发送失败");
            return false;
        }

    }
}
