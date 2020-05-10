package com.kk.community.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kk.community.entity.Comment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 工具类，辅助注册
 */
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密 密码加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //Json格式转Comment对象
    public static Comment getJSONClass(String json){

        return null;

    }

    //将键值对集转为Json格式
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for (String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //将键值对转为Json格式
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    //将键值对转为Json格式
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }


}
