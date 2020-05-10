package com.kk.community.service.impl;

import com.github.pagehelper.Page;
import com.kk.community.dao.MessageMapper;
import com.kk.community.entity.Message;
import com.kk.community.service.MessageService;
import com.kk.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : K k
 * @date : 19:33 2020/5/1
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public Page<Message> findConversations(int userId) {
        return messageMapper.selectConversations(userId);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public Page<Message> findLetters(String conversationId) {
        return messageMapper.selectLetters(conversationId);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids,1);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId,topic);
    }

    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId,topic);
    }

    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }

    @Override
    public Page<Message> findNotices(int userId, String topic) {
        return messageMapper.selectNotices(userId,topic);
    }

    @Override
    public void oneReaded(int userId) {
        messageMapper.updateSystemStatus(userId);
    }
}
