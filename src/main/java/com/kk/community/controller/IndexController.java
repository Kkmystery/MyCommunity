package com.kk.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : K k
 * @date : 15:32 2020/5/1
 */
@Controller
@RequestMapping("/refresh")
public class IndexController {

    @RequestMapping
    public String globalRefresh(Model model) {
        List<Map<String,String>> lists = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("author", "曹雪芹");
        map.put("title", "《红楼梦》");
        map.put("url", "www.baidu.com");
        lists.add(map);

        // 用作对照
        model.addAttribute("refresh", "我不会被刷新");

        model.addAttribute("title", "我的书单");
        model.addAttribute("books", lists);
        return "test";
    }

    /**
     * 局部刷新，注意返回值
     * @param model
     * @return
     */
    @RequestMapping("/local")
    public String localRefresh(Model model) {
        List<Map<String,String>> lists = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("author", "罗贯中");
        map.put("title", "《三国演义》");
        map.put("url", "www.baidu.com");
        lists.add(map);

        model.addAttribute("title", "我的书单");
        model.addAttribute("books", lists);
        // "test"是test.html的名，
        // "table_refresh"是test.html中需要刷新的部分标志,
        // 在标签里加入：th:fragment="table_refresh"
        return "/site/test::table_refresh";
    }
}
