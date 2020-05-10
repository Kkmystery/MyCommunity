package com.kk.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : K k
 * @date : 17:10 2020/4/30
 */
@SpringBootTest
class SensitiveFilterTest {

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test(){
        String text1="赌博";
        String text2="你他妈的操你妈啊";
        String text3="来嫖娼不";
        String text4="吸毒？";
        String text5="敢赌博吗";
        String filter = sensitiveFilter.filter(text2);
        System.out.println(filter);
    }
}