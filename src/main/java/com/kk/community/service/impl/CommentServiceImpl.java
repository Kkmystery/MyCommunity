package com.kk.community.service.impl;

import com.github.pagehelper.Page;
import com.kk.community.dao.CommentMapper;
import com.kk.community.entity.Comment;
import com.kk.community.service.CommentService;
import com.kk.community.service.DiscussPostService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.EmojiUtil;
import com.kk.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : K k
 * @date : 10:23 2020/5/1
 */
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Resource
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    private EmojiUtil emojiUtil;

    @Override
    public Page<Comment> findCommentByEntity(int entityType, int entityId) {
        return commentMapper.selectList(entityType,entityId);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //将表情转换
        comment.setContent(EmojiUtil.emojiConverterToAlias(comment.getContent()));
        int rows=commentMapper.insert(comment);
        //更新评论数量
        if (comment.getEntityType()==ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

    @Override
    public Comment selectCommentById(int id) {
        return commentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<Comment> findMyComment(int userId) {
        return commentMapper.selectMyCommentList(userId);
    }

    @Override
    public int findMyCommentCount(int userId) {
        return commentMapper.selectMyCommentCount(userId);
    }

    @Override
    public void deleteComment(int id, int status) {
        commentMapper.updateStatus(id,status);
    }

}
