package com.kk.community.service;

import com.github.pagehelper.Page;
import com.kk.community.entity.Message;

import java.util.List;

/**
 * @author : K k
 * @date : 19:33 2020/5/1
 */
public interface MessageService {
    //分页查询会话
    public Page<Message> findConversations(int userId);
    //会话数量
    public int findConversationCount(int userId);
    //分页查询私信
    public Page<Message> findLetters(String conversationId);
    //私信数量
    public int findLetterCount(String conversationId);
    //私信中未读的数量
    public int findLetterUnreadCount(int userId,String conversationId);
    //新增会话
    public int addMessage(Message message);
    //读取消息
    public int readMessage(List<Integer> ids);
    //查询某个主题下最新消息 userId是to_id
    Message findLatestNotice(int userId,String topic);
    //查询某个主题所包含的通知数量 userId是to_id
    int findNoticeCount(int userId,String topic);
    //查询未读的通知的数量
    int findNoticeUnreadCount(int userId,String topic);
    //查询某一通知的所以会话
    public Page<Message> findNotices(int userId,String topic);
    //一键已读，将所有未读消息更新
    public void oneReaded(int userId);
}
