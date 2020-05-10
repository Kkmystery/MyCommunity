package com.kk.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.entity.DiscussPost;
import com.kk.community.entity.User;
import com.kk.community.service.DiscussPostService;
import com.kk.community.service.LikeService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Resource
    private DiscussPostService DiscussPostservice;
    @Resource
    private UserService userService;
    @Autowired
    private LikeService likeService;

    /*分页查询*/
    @GetMapping(value = "/index")
    public String getIndexPage(Model model, @RequestParam(value = "userId", required = false) Integer userId,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize,
                               @RequestParam(name = "orderMode",defaultValue = "0")int orderMode) {
        PageHelper.startPage(page, pageSize);
        //System.out.println(userId);
        Page<DiscussPost> list = DiscussPostservice.selectListByPage(userId,orderMode);
        //统一跳转路径
        String pagePath="/index?orderMode="+orderMode;
        model.addAttribute("pagePath",pagePath);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post :
                    list) {
                //将帖子和对应发表用户封装在一个map中
                //System.out.println(post);
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page",list);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
