package com.kk.community.dao;

import com.kk.community.entity.LoginTicket;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : shujuelin
 * @date : 22:43 2020/4/29
 */
@SpringBootTest
class LoginTicketMapperTest {
    @Resource
    LoginTicketMapper mapper;

    @Test
    void insert() {
        LoginTicket ticket=new LoginTicket();
        ticket.setTicket(CommunityUtil.generateUUid());
        ticket.setUserId(1);
        ticket.setStatus(1);
        ticket.setExpired(new Date());

        mapper.insert(ticket);
    }

    @Test
    void selectByTicket() {
        LoginTicket ticket = mapper.selectByTicket("ddfeb8cf054a42b8a817785fb979d42d");
        System.out.println(ticket.toString());
    }

    @Test
    void updateStatus() {
        mapper.updateStatus("ddfeb8cf054a42b8a817785fb979d42d",1);
    }
}