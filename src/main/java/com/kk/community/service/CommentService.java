package com.kk.community.service;

import com.github.pagehelper.Page;
import com.kk.community.dao.CommentMapper;
import com.kk.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : K k
 * @date : 10:19 2020/5/1
 */
public interface CommentService {
    //查询分页
    public Page<Comment> findCommentByEntity(int entityType, int entityId);
    //查询评论数
    public int findCommentCount(int entityType, int entityId);
    //添加评论
    public int addComment(Comment comment);
    //查找评论
    public Comment selectCommentById(int id);

    //查找个人回复记录
    public Page<Comment> findMyComment(int userId);
    //查找个人回复数
    public int findMyCommentCount(int userId);
    //删除评论(当帖子被删除时，评论也需要删除)
    public void deleteComment(int id,int status);
}
