package com.kk.community.util;

import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : K k
 * @date : 11:24 2020/4/30
 */
public class CookiesUtil {
    public static String getValue(HttpServletRequest request, String name){
        if(request==null||name==null){
            throw new IllegalArgumentException("参数为空！");
        }

        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
            for (Cookie cookie:cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
