package com.kk.community.service.impl;

import com.github.pagehelper.Page;
import com.kk.community.dao.DiscussPostMapper;
import com.kk.community.entity.DiscussPost;
import com.kk.community.service.DiscussPostService;
import com.kk.community.util.SensitiveFilter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public Page<DiscussPost> selectListByPage(Integer userId,int orderMode) {
        Page<DiscussPost> discussPostList=discussPostMapper.selectListByPage(userId,orderMode);
        if(discussPostList!=null) return discussPostList;
        else return null;
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        //CKEDITOR富文本已经将文本内容转义为html了
        //post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertSelective(post);
    }

    @Override
    public DiscussPost findDiscussPostById(Integer id) {
        return discussPostMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id, int Status) {
        return discussPostMapper.updateStatus(id, Status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id,score);
    }

    @Override
    public Page<DiscussPost> findMyDiscussPost(int userId) {
        return discussPostMapper.selectMyListByPage(userId);
    }

    @Override
    public int findMyDiscussPostCount(int userId) {
        return discussPostMapper.selectMyDiscussPostCount(userId);
    }
}
