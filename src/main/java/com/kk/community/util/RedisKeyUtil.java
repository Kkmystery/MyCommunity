package com.kk.community.util;

/**
 * @author : K k
 * @date : 22:35 2020/5/2
 */
public class RedisKeyUtil {
    private static final long serialVersionUID = -526324944915280489L;
    private static final String SPILT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_FOLLOWEE="followee";
    private static final String PREFIX_FOLLOWER="follower";
    private static final String PREFIX_KAPTCHA="kaptcha";
    private static final String PREFIX_TICKET="ticket";
    private static final String PREFIX_USER="user";
    private static final String PREFIX_UV="uv";
    private static final String PREFIX_DAU="dau";
    private static final String PREFIX_POST="post";

    //某个实体的赞
    //like:entity:entityType:entityId作为键 -> set(userId)  包括帖子类型1，评论类型2
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPILT+entityType+SPILT+entityId;
    }
    //某个用户的被赞数
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPILT+userId;
    }

    //某个用户关注的实体 翻译为 KEY：谁关注了什么类型的东西 VALUE：关注的类型的id和关注时间
    //followee:userId:entityType -> zset(entityId,nowTime)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPILT+userId+SPILT+entityType;
    }

    //某个用户拥有的粉丝 翻译为 KEY：用户（entityType），用户id（entityId） VALUE：关注他的所有用户的id和关注时间
    //follower:entityType:entityId -> zset(userId,nowTime)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPILT+entityType+SPILT+entityId;
    }

    //登陆验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPILT+owner;
    }
    //登陆凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPILT+ticket;
    }
    //用户信息缓存
    public static String getUserKey(int userId){
        return PREFIX_USER+SPILT+userId;
    }

    //单日UV
    public static String getUVKey(String date){
        return PREFIX_UV+SPILT+date;
    }
    //区间UV
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV+SPILT+startDate+SPILT+endDate;
    }

    //单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPILT+date;
    }
    //区间活跃用户
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPILT+startDate+SPILT+endDate;
    }

    //热帖排行
    public static String getPostScoreKey(){
        return PREFIX_POST+SPILT+"score";
    }
}
