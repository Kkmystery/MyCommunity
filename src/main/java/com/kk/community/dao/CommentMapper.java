package com.kk.community.dao;

import com.github.pagehelper.Page;
import com.kk.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKeyWithBLOBs(Comment record);

    int updateByPrimaryKey(Comment record);

    Page<Comment> selectList(int entityType,int entityId);

    int selectCountByEntity(int entityType,int entityId);

    Page<Comment> selectMyCommentList(int userId);
}