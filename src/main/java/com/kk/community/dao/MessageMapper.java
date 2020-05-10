package com.kk.community.dao;

import com.github.pagehelper.Page;
import com.kk.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKeyWithBLOBs(Message record);

    int updateByPrimaryKey(Message record);

    //查询会话分页
    Page<Message> selectConversations(int userId);
    //查询当前用户会话列的数量
    int selectConversationCount(int userId);
    //查询某个会话的私信列表
    Page<Message> selectLetters(String conversationId);
    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);
    //查询未读私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);
    //修改消息状态 已读未读
    int updateStatus(List<Integer> ids,int status);
    //查询某个主题下最新消息 userId是to_id
    Message selectLatestNotice(int userId,String topic);
    //查询某个主题所包含的通知数量 userId是to_id
    int selectNoticeCount(int userId,String topic);
    //查询未读的通知的数量
    int selectNoticeUnreadCount(int userId,String topic);
    //某个主题所包含的通知列表
    Page<Message> selectNotices(int userId,String topic);
    //将未读消息一键读取（系统消息）
    void updateSystemStatus(int userId);
}