package com.kk.community.controller;

import com.kk.community.entity.Comment;
import com.kk.community.entity.DiscussPost;
import com.kk.community.service.ElasticsearchService;
import com.kk.community.service.LikeService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : K k
 * @date : 13:13 2020/5/7
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search" ,method = RequestMethod.GET)
    public String search(String keyword, Model model,
                         @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                         @RequestParam(value = "pageSize", required = false, defaultValue = "8") int pageSize){
        if (keyword.equals("")){
            return "index";
        }

        //搜素帖子
        Page<DiscussPost> searchResult = elasticsearchService.searchDiscussPost(keyword, page - 1, pageSize);
        String pagePath = "/search?keyword=" + keyword;
        //当Entity_Type=1的时候，Entity_id代表是帖子的id
        com.github.pagehelper.Page<DiscussPost> discussPostList=new com.github.pagehelper.Page<>();
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(searchResult!=null){
            for (DiscussPost post:searchResult){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                //用于分页
                discussPostList.add(post);
                discussPosts.add(map);
            }
        }
        discussPostList.setPages(searchResult.getTotalPages());
        discussPostList.setPageNum(page);
        //用于前端分页
        model.addAttribute("page", discussPostList);
        model.addAttribute("pagePath", pagePath);
        model.addAttribute("keyword",keyword);
        model.addAttribute("discussPosts",discussPosts);
        return "site/search";
    }
}
