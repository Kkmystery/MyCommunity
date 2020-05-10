package com.kk.community.controller.advice;

import com.kk.community.util.CommunityUtil;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author : K k
 * @date : 10:55 2020/5/2
 */
//异常处理模板
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger= LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发送异常:"+e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());
        }
        //截取异步请求出错 并返回json错误信息
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset-utf-8");
            PrintWriter writer=response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
