package com.kk.community;

import com.kk.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@SpringBootTest
public class mailTest {
    @Resource
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void sendMail(){
        mailClient.sendMail("1161676129@qq.com","通知","八八八啦啦啦");
    }

    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1161676129@qq.com","HTML",content);
    }
}
