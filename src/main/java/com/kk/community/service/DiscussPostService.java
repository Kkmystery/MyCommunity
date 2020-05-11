package com.kk.community.service;

import com.github.pagehelper.Page;
import com.kk.community.entity.DiscussPost;

public interface DiscussPostService {
    //查询所有页
    Page<DiscussPost> selectListByPage(Integer userId,int orderMode);
    //插入新帖子
    public int addDiscussPost(DiscussPost post);
    //查询帖子内容
    public DiscussPost findDiscussPostById(Integer id);
    //新增评论数
    public int updateCommentCount(int id,int commentCount);
    //加精置顶
    public int updateType(int id,int type);
    public int updateStatus(int id,int status);

    //更新热榜分数
    public int updateScore(int id,double score);

    //显示个人创作
    public Page<DiscussPost> findMyDiscussPost(int userId);
    //查找个人创作的数目
    public int findMyDiscussPostCount(int userId);

}
