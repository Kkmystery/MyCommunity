package com.kk.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@MapperScan("com.kk.community.dao")
@SpringBootApplication
@EnableAspectJAutoProxy
public class CommunitydemoApplication {
    @PostConstruct
    public void init(){
        //解决netty启动冲突问题
        System.setProperty("es.set.netty.runtime.available.processors","false");

    }

    public static void main(String[] args) {
        SpringApplication.run(CommunitydemoApplication.class, args);
    }

}
