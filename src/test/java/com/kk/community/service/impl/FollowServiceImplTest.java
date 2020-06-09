package com.kk.community.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kk.community.service.CommentService;
import com.kk.community.service.DiscussPostService;
import com.kk.community.service.FollowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : K k
 * @date : 20:43 2020/5/3
 */
@SpringBootTest
class FollowServiceImplTest {

    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;

    @Test()
    public void deleteDiscuss(){
        commentService.findCommentByEntity(1,283);

    }


    @Test
    void findFollowees() {
        int pageNum=2,pageSize=5;
        Page<Map<String, Object>> followees = followService.findFollowees(164, pageNum, pageSize);
        long followeeCount = followService.findFolloweeCount(164, 3);
        int pages=0;
        int temp=0;
        while(temp<(int)followeeCount){
            temp+=pageSize;
            pages++;
        }

        followees.setPages(pages);

        for (Map<String, Object> map :
                followees) {
            System.out.println(map.get("user"));
            System.out.println(map.get("followTime"));
        }
        System.out.println(followees.getPageNum());
        System.out.println(followees.getPages());

    }

    @Test
    void findFollowers() {
    }
}