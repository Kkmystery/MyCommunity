package com.kk.community.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.entity.Comment;
import com.kk.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CommentMapperTest {
    @Resource
    CommentMapper mapper;
    @Resource
    MessageMapper messageMapper;

    @Test
    void selectList() {
        Page<Comment> comments = mapper.selectList(1,1);
        System.out.println(comments);
    }

    @Test
    void test(){
        String s="content=qwdasd&entityType=2&entityId=246&targetId=165";
        String[] split = s.split("&");
        for (String s1 :
                split) {
            String[] split2 = s1.split("=");
        }
    }

    @Test
    void messageTest(){
        PageHelper.startPage(0,20);
        /*Page<Message> messages = messageMapper.selectConversations(111);
        for (Message m :
                messages) {
            System.out.println(m.toString());
        }
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);*/
        List<Message> messages1 = messageMapper.selectLetters("111_112");
        for (Message m :
                messages1) {
            System.out.println(m);
        }
        int i2 = messageMapper.selectLetterCount("111_112");
        System.out.println(i2);
        int i1 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i1);
    }

}