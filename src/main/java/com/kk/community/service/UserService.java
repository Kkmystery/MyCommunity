package com.kk.community.service;

import com.kk.community.entity.LoginTicket;
import com.kk.community.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    //通过Id查找
    User findUserById(Integer userId);
    //注册
    Map<String,Object> register(User user);
    //激活账号 分为成功、失败、重复使用无效激活码
    public int activation(int userId,String code);
    //登陆功能 分为失败和成功
    public Map<String,Object> login(String username,String password,int expiredSeconds);
    //退出登陆
    public void logout(String ticket);
    //查询凭证
    public LoginTicket findLoginTicket(String ticket);
    //上传文件头像更新用户信息
    public int updateHeader(int userId,String headerUrl);
    //用户名查询
    public User findByUserName(String username);
    //认证权限信息
    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
