package com.kk.community.service;

import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author : K k
 * @date : 19:04 2020/5/3
 */
public interface FollowService {

    //关注
    public void follow(int userId,int entityType,int entityId);
    //取关
    public void unfollow(int userId,int entityType,int entityId);
    //查询关注的实体的数量
    public long findFolloweeCount(int userId,int entityType);
    //查询实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId);
    //查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId,int entityType,int entityId);
 /*   //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId);
    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId);*/
    //查询某用户关注的人
    public Page<Map<String,Object>> findFollowees(int userId,int pageNum,int pageSize);
    //查询某用户的粉丝
    public Page<Map<String,Object>> findFollowers(int userId,int pageNum,int pageSize);
    //查询某用户的粉丝的ids
    public List<Integer> findFollowersId(int userId);
}
