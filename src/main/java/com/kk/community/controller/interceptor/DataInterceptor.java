package com.kk.community.controller.interceptor;

import com.kk.community.entity.User;

import com.kk.community.service.DataService;
import com.kk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : K k
 * @date : 21:11 2020/5/8
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        //统计DAU
        User user = hostHolder.getUser();
        if (user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
