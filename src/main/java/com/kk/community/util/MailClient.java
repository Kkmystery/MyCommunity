package com.kk.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送
 */
@Component
public class MailClient {

    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content /*String filePlace*/) {
        try {
            MimeMessage mimeMailMessage=mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMailMessage,true);
            //邮件设置
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content,true);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            //上传文件
           // mimeMessageHelper.addAttachment("名字",new File("绝对路径"));
            mailSender.send(mimeMailMessage);
        } catch (MessagingException e) {
            logger.error("发送失败",e.getMessage());
        }


    }
}
