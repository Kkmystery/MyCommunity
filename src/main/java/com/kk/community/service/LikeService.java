package com.kk.community.service;

/**
 * @author : K k
 * @date : 9:58 2020/5/3
 */
public interface LikeService {
    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId);
    //查询某实体的点赞数
    public long findEntityLikeCount(int entityType,int entityId);
    //查询某人对某实体点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId);
    //查询用户一共收到的赞的个数
    public int findUserLikeCount(int userId);
}
