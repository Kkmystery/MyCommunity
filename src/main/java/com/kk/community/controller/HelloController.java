package com.kk.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.dao.CommentMapper;
import com.kk.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class HelloController {
    @Resource
    private CommentMapper mapper;

    @GetMapping("/hello/{id}")
    public Comment hello(@PathVariable("id")Integer id){
        Comment comment = mapper.selectByPrimaryKey(id);
        return comment;
    }

    @PostMapping(value = "/search")
    public Page<Comment> getall(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "2") int pageSize)

    {
        PageHelper.startPage(page,pageSize);
        Page<Comment> commentList = mapper.selectList(1,1);
        return commentList;
    }

}
