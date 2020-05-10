package com.kk.community.config;

import com.kk.community.controller.interceptor.DataInterceptor;
import com.kk.community.controller.interceptor.LoginRequiredInterceptor;
import com.kk.community.controller.interceptor.LoginTicketIntercepter;
import com.kk.community.controller.interceptor.MessageInterceptor;
import com.kk.community.entity.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : K k
 * @date : 11:12 2020/4/30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketIntercepter loginTicketIntercepter;
    /*@Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;*/
    @Autowired
    private MessageInterceptor messageInterceptor;
    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //验证是否有登陆令牌  作用于前端可显示的部分，若登陆则显示头像等信息
        registry.addInterceptor(loginTicketIntercepter)
                .excludePathPatterns("/**/*css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        //验证是否是登陆状态  防止url请求直接访问资源 对有@LoginRequired注解的方法进行拦截
//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

    }
}
