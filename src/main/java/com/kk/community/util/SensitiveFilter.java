package com.kk.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.tree.TreeNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : K k
 * @date : 16:24 2020/4/30
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换的符号
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct //标记初始化方法 在容器Bean被服务器初始化时候 也同时初始化这个方法
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword=reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e){
            logger.error("加载敏感词文件失败"+e.getMessage());
        }
    }

    /**
     * @param text
     * @return 过滤后的字符串
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode=rootNode;
        //指针2
        int begin=0;
        //制作3
        int position=0;
        //结果字符串
        StringBuilder sb=new StringBuilder();

        while (position<text.length()){
            char c=text.charAt(position);
            //跳过符号
            if(isSysmbol(c)){
                //若指针1是根节点,此符号记录入结果，指针2向下走一步
                if (tempNode ==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode=tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position=++begin;
                //重新指向根节点
                tempNode=rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词,将begin到position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一位置
                begin=++position;
                //重新指向跟节点
                tempNode=rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符加入 即指针3先到尾
        sb.append(text.substring(begin));
        return sb.toString();
    }
    //判断是否为符号
    private boolean isSysmbol(Character c){
        //c< 0x2E80||c>0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c< 0x2E80||c>0x9FFF);
    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TrieNode tempNode=rootNode;
        for (int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //初始化子结点
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子结点
            tempNode =subNode;
            //设置结束标识
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }


    //定义前缀树
    private class TrieNode {
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //当前节点子结点 key是下级字符，value是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();


        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子结点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
