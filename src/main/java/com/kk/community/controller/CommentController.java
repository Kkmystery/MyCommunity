package com.kk.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.entity.Comment;
import com.kk.community.entity.DiscussPost;
import com.kk.community.entity.User;
import com.kk.community.event.EventProducer;
import com.kk.community.service.CommentService;
import com.kk.community.service.DiscussPostService;
import com.kk.community.service.UserService;
import com.kk.community.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    @ResponseBody
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        if (comment.getContent()==null||comment.getContent().equals("")){
            return CommunityUtil.getJSONString(1,"输入不合法！");
        }

        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.selectCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
            // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPostId);
    }
        return CommunityUtil.getJSONString(0,"评论成功");
    }

    @RequestMapping(path = "/myReply/{userId}",method = RequestMethod.GET)
    public String getMyReply(@PathVariable("userId")int userId, Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "12") int pageSize){
        User user = userService.findUserById(userId);
        PageHelper.startPage(page,pageSize);
        Page<Comment> myComments = commentService.findMyComment(userId);
        List<Map<String,Object>> myCommentList=new ArrayList<>();
        if (myComments!=null){
            for (Comment comment:myComments){
                Map<String,Object> map=new HashMap<>();
                map.put("comment1",comment);
                //System.out.println("commententityId"+comment.getEntityId());
                DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
                map.put("post1",post);
                myCommentList.add(map);
            }
        }
        int myCommentCount = commentService.findMyCommentCount(userId);
        model.addAttribute("myCommentCount",myCommentCount);
        String pagePath = "/comment/myReply/" + userId;
        model.addAttribute("pagePath",pagePath);
        model.addAttribute("page",myComments);
        model.addAttribute("myCommentList",myCommentList);
        model.addAttribute("user",user);

        return "site/my-reply";

    }
}