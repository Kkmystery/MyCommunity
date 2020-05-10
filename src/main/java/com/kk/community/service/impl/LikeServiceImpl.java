package com.kk.community.service.impl;

import com.kk.community.service.LikeService;
import com.kk.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author : K k
 * @date : 9:38 2020/5/3
 */
@Service
public class LikeServiceImpl implements LikeService {


    @Autowired
    private RedisTemplate redisTemplate;

    //entityType是类型 比如帖子，评论，用户。 entityId是若为帖子则为discusspost的id，若为评论，则为comment的id
    //userId为点赞人的id，这个id是User表中的id
    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId){
        /*String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }*/
        //redis事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                //用户一共收到的赞的数量
                String userLikeKey= RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember=redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi(); //提交事务
                if(isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else{
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }

    //查询某个实体的点赞数
    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }
    //查询用户对实体的点赞状态，分为已赞和未赞
    //1表示当前用户已点，0表示未点赞
    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0; //1表示当前用户已点，0表示未点赞
    }

    //查询用户收到的赞的数量
    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey=RedisKeyUtil.getUserLikeKey(userId);
        //用户收到的赞的数量
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0: count.intValue();
    }
}
