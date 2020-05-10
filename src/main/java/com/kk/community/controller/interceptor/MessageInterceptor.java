package com.kk.community.controller.interceptor;

import com.kk.community.entity.User;
import com.kk.community.service.MessageService;
import com.kk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : K k
 * @date : 16:49 2020/5/5
 */
@Component
public class MessageInterceptor  implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user =hostHolder.getUser();
        if (user!=null&& modelAndView!=null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            if(letterUnreadCount!=0&&noticeUnreadCount!=0){
                modelAndView.addObject("UnreadCount","("+letterUnreadCount+"条私信/"+noticeUnreadCount+"条通知)");
            }else if(letterUnreadCount != 0){
                modelAndView.addObject("UnreadCount","("+letterUnreadCount+"条私信)");
            }else if(noticeUnreadCount!=0){
                modelAndView.addObject("UnreadCount","("+noticeUnreadCount+"条通知)");
            }else{
                modelAndView.addObject("UnreadCount",null);
            }
            modelAndView.addObject("allUnreadCount",letterUnreadCount+noticeUnreadCount);
        }
    }
}
