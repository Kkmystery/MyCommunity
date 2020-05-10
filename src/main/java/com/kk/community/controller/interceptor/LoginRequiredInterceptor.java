package com.kk.community.controller.interceptor;

import com.kk.community.annotation.LoginRequired;
import com.kk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author : K k
 * @date : 15:52 2020/4/30
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            //若为方法类型,且标记了@LoginRequired则限制访问,并返回登陆页面
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired!=null && hostHolder.getUser()==null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }

}
