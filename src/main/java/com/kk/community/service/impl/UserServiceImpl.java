package com.kk.community.service.impl;

import com.kk.community.dao.LoginTicketMapper;
import com.kk.community.dao.UserMapper;
import com.kk.community.entity.LoginTicket;
import com.kk.community.entity.User;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.CommunityUtil;
import com.kk.community.util.MailClient;
import com.kk.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    //判断用户名的书写规范
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{4,10}$";
    private static final String EMAIL_PATTERN = "^\\w+@\\w+(\\.[a-zA-Z]{2,3}){1,2}$";

    @Resource
    UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
   /* @Resource
    private LoginTicketMapper loginTicketMapper;*/
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domin;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(Integer userId) {
        //return userMapper.selectByPrimaryKey(userId);
        User user=getCache(userId);
        if (user==null){
            initCache(userId);
        }
        return user;
    }

    /*发送验证邮箱 并将用户存入数据库*/
    public Map<String,Object> register(User user){

        Map<String ,Object> map=new HashMap<>();
        //出现错误信息，则向map中添加
        //空值判断
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(!user.getUsername().matches(USERNAME_PATTERN)){
            map.put("usernameMsg","用户名由英文字母和数字组成的4-16位字符，以字母开头！");
            return map;
        }
        if(!user.getPassword().matches(PASSWORD_PATTERN)){
            map.put("passwordMsg","密码不能含有非法字符，长度在4-10之间！");
            return map;
        }
        if(!user.getEmail().matches(EMAIL_PATTERN)){
            map.put("emailMsg","Email格式不正确，例如1020803066@qq.com");
            return map;
        }

        //验证账号
        User u1=userMapper.selectByName(user.getUsername());
        if(u1!=null){
            map.put("usernameMsg","该账户已存在！");
            return map;
        }
        //验证邮箱
        User u2=userMapper.selectByEmail(user.getEmail());
        if(u2!=null){
            map.put("emailMsg","该账户邮箱已被注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUid().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setStatus(0); //未激活
        user.setType(0);//标识为普通用户
        user.setActivationCode(CommunityUtil.generateUUid().substring(0,5));
        user.setHeaderUrl(domin+contextPath+"/user/header/man.jpg");
        user.setCreateTime(new Date());
        int id = userMapper.insert(user);
        //发送激活邮件
        Context context=new Context();
        context.setVariable("username",user.getUsername());
        //http://localhost:8080/community/activation/101/code
        String url=domin+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        //System.out.println(content);
        mailClient.sendMail(user.getEmail(),"KK论坛激活账号",content);

        return map;
    }


    /*激活账号
    * 检查账号的激活状态，并返回激活验证码*/
    public int activation(int userId,String code){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            user.setStatus(1);
            userMapper.updateByPrimaryKey(user);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /*登陆功能*/
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String ,Object> map=new HashMap<>();
        //空值处理 前端可以进行空值处理
        /*if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }*/
        //验证账号
        User user=userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //验证激活状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //到了这步，说明账号存在且输入无误，接下来生成登陆凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUid());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //loginTicketMapper.insert(loginTicket);
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket); //转Json格式

        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    /*退出功能,将状态置为1 表示无效令牌*/
    @Override
    public void logout(String ticket) {
        //1是无效
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    /*用户过滤器查找登陆凭证*/
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    /*更新用户信息*/
    @Override
    public int updateHeader(int userId, String headerUrl) {
        User user=userMapper.selectByPrimaryKey(userId);
        user.setHeaderUrl(headerUrl);
        int rows=userMapper.updateByPrimaryKey(user);
        clearCache(userId);
        return rows;
    }

    @Override
    public User findByUserName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user=this.findUserById(userId);
        List<GrantedAuthority> list=new ArrayList<>();
        list.add((GrantedAuthority) () -> {
            switch (user.getType()){
                case 0:
                    return AUTHORITY_USER;
                case 1:
                    return AUTHORITY_ADMIN;
                default:
                    return AUTHORITY_MODERATOR;
            }
        });
        return list;
    }

    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到时初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectByPrimaryKey(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
