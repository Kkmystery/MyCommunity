package com.kk.community.service.impl;

import com.github.pagehelper.Page;
import com.kk.community.entity.User;
import com.kk.community.service.FollowService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.RedisKeyUtil;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : K k
 * @date : 19:04 2020/5/3
 */
@Service
public class FollowServiceImpl implements FollowService , CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }
    //取消关注
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
                //提交
                redisOperations.multi();

                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);
                //执行
                return redisOperations.exec();
            }
        });
    }
    //查询关注的实体的数量
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //查询实体的粉丝数量
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //查询当前用户是否已关注该实体(即查该实体的粉丝里面有没有我）
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

   /* //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, 0, -1);
        if(targetIds==null){
            return null;
        }
        //Page<Map<String,Object>> list= (Page<Map<String, Object>>) new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId:targetIds){
            Map<String, Object> map=new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, 0, -1);

        if (targetIds==null){
            return null;
        }

        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId:targetIds){
            Map<String, Object> map=new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }*/

    @Override
    public Page<Map<String, Object>> findFollowees(int userId, int pageNum, int pageSize) {
        //分页
        Page<Map<String, Object>> page=new Page<>();
        int start=(pageNum-1)*pageSize;
        int end=pageNum*pageSize-1;

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Long total = redisTemplate.opsForZSet().zCard(followeeKey);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, start, end);
        if(targetIds==null){
            return null;
        }

        //Page<Map<String,Object>> list= (Page<Map<String, Object>>) new ArrayList<Map<String,Object>>();
       // List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId:targetIds){
            Map<String, Object> map=new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            page.add(map);
        }

        page.setPageNum(pageNum);

        long followeeCount = this.findFolloweeCount(userId, ENTITY_TYPE_USER);
        int pages=0;
        int temp=0;
        while(temp<(int)followeeCount){
            temp+=pageSize;
            pages++;
        }
        page.setPages(pages);
        return page;
    }

    @Override
    public Page<Map<String, Object>> findFollowers(int userId, int pageNum, int pageSize) {
        //分页
        Page<Map<String, Object>> page=new Page<>();
        int start=(pageNum-1)*pageSize;
        int end=pageNum*pageSize-1;

        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Long total = redisTemplate.opsForZSet().zCard(followerKey);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, start, end);
        if(targetIds==null){
            return null;
        }

        //Page<Map<String,Object>> list= (Page<Map<String, Object>>) new ArrayList<Map<String,Object>>();
        // List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId:targetIds){
            Map<String, Object> map=new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            page.add(map);
        }

        page.setPageNum(pageNum);

        long followerCount = this.findFollowerCount(userId, ENTITY_TYPE_USER);
        int pages=0;
        int temp=0;
        while(temp<(int)followerCount){
            temp+=pageSize;
            pages++;
        }
        page.setPages(pages);
        return page;
    }

    @Override
    public List<Integer> findFollowersId(int userId) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, 0, -1);
        List<Integer> ids=new ArrayList<>();
        for (Integer id:targetIds){
            ids.add(id);
        }
        return ids;
    }
}
