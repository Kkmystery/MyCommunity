package com.kk.community.util;

import com.kk.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author : K k
 * @date : 11:40 2020/4/30
 * 持有用户信息，代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
