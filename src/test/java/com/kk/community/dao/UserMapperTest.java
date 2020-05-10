package com.kk.community.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.entity.DiscussPost;
import com.kk.community.entity.User;
import com.kk.community.service.DiscussPostService;
import com.kk.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {
    @Resource
    UserMapper mapper;
    @Resource
    DiscussPostService DiscussPostservice;
    @Test
    void selectByPrimaryKey() {
        int page=1,pageSize=10;
        PageHelper.startPage(page, pageSize);
        Integer userId=null;
        Page<DiscussPost> list = DiscussPostservice.selectListByPage(userId,0);
        System.out.println(list.getCountColumn());
        System.out.println(list.getTotal());
        System.out.println(list.getPageNum());
        System.out.println(list.getPages());
        System.out.println(list.getPageSizeZero());
        System.out.println(list.getPageSize());
        System.out.println(list.getStartRow());
        System.out.println(list.getEndRow());
    }

    @Test
    void test(){
        User liubei = mapper.selectByName("liubei");
        System.out.println(liubei);
        User user = mapper.selectByEmail("nowcoder134@sina.com");
        System.out.println(user);
    }
}