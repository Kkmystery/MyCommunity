package com.kk.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kk.community.annotation.LoginRequired;
import com.kk.community.entity.User;
import com.kk.community.event.EventProducer;
import com.kk.community.service.FollowService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.CommunityUtil;
import com.kk.community.util.Event;
import com.kk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : K k
 * @date : 19:12 2020/5/3
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType,int entityId){
        User user=hostHolder.getUser();
        if(user==null){

        }
        followService.follow(user.getId(),entityType,entityId);
        //发布通知 触发关注事件
        Event event=new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user=hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Model model,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ){
        User user =userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        //分页
        String pagePath = "/followees/" + userId;
        model.addAttribute("pagePath", pagePath);
        Page<Map<String, Object>> userList = followService.findFollowees(userId,page,pageSize);
        model.addAttribute("page",userList);
        if(userList!=null){
            for(Map<String,Object> map:userList){
                User u= (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Model model,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ){
        User user =userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        //分页
        String pagePath = "/followers/" + userId;
        model.addAttribute("pagePath", pagePath);
        Page<Map<String, Object>> userList = followService.findFollowers(userId,page,pageSize);
        model.addAttribute("page",userList);
        if(userList!=null){
            for(Map<String,Object> map:userList){
                User u= (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/follower";
    }

    //判断这个用户是否被我关注
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }
}
