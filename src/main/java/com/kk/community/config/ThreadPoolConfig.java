package com.kk.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : K k
 * @date : 13:15 2020/5/9
 */
@Configuration
@EnableScheduling   //开启定时
@EnableAsync  //开启异步
public class ThreadPoolConfig {

}
