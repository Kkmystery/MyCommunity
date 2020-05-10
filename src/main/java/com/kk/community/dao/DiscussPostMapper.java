package com.kk.community.dao;

import com.github.pagehelper.Page;
import com.kk.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiscussPostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DiscussPost record);

    int insertSelective(DiscussPost record);

    DiscussPost selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DiscussPost record);

    int updateByPrimaryKeyWithBLOBs(DiscussPost record);

    int updateByPrimaryKey(DiscussPost record);

    /*分页查询*/
    Page<DiscussPost> selectListByPage(Integer userId,int orderMode);

    /*分页查询*/
    Page<DiscussPost> selectMyListByPage(Integer userId);

    //查询帖子数目
    int selectMyDiscussPostCount(int userId);

    //更新帖子的评论数 （当评论新增时)
    int updateCommentCount(int id,int commentCount);

    //置顶，加精
    int updateType(int id,int type);
    int updateStatus(int id,int status);

    int updateScore(int id,double score);

}