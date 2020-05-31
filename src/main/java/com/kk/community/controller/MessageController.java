package com.kk.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.annotation.LoginRequired;
import com.kk.community.entity.Message;
import com.kk.community.entity.User;
import com.kk.community.service.MessageService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.CommunityUtil;
import com.kk.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.objects.XNull;
import lombok.experimental.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author : K k
 * @date : 20:29 2020/5/1
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "8") int pageSize) {
        User user = hostHolder.getUser();
        PageHelper.startPage(page, pageSize);
        String pagePath = "/letter/list";
        model.addAttribute("pagePath", pagePath);
        //会话列表
        Page<Message> conversationList = messageService.findConversations(user.getId());
        model.addAttribute("page", conversationList);
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //这里有个小BUG就是关于int和Integer不能直接用==比较
                int targetId = 0;
                int from = user.getId();
                if (user.getId().equals(message.getFromId())) {
                    targetId = message.getToId();
                } else {
                    targetId = message.getFromId();
                }
                map.put("target", userService.findUserById(targetId));
               /* System.out.println(userService.findUserById(targetId));
                System.out.println(targetId);
                System.out.println(user.getId());
                System.out.println(message.getFromId());
                System.out.println(message.getToId());*/
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/letter";
    }

    //聊天页
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  Model model) {
        //分页信息
        PageHelper.startPage(page, pageSize);
        //一个私信会话中的所有信息
        Page<Message> letterList = messageService.findLetters(conversationId);
        model.addAttribute("page", letterList);
        String pagePath = "/letter/detail/" + conversationId;
        model.addAttribute("pagePath", pagePath);
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids=getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "site/letter-detail";
    }

    //返回目标
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId().equals(id0)) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    //得到未读的消息列表
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                   if(hostHolder.getUser().getId().equals(message.getToId()) && message.getStatus()==0);
                        ids.add(message.getId());
            }
        }
        return ids;
    }

    //发送私信
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        // TODO 在私信栏新增 点击发送目标框 提示关注列表 然后点击名字发送确定发送人
        User target = userService.findByUserName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String,Object> messageVo=new HashMap<>();
        if (message!=null){
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data= JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId"))); //消息触发人
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("count",count );
            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unread",unread);
            model.addAttribute("commentNotice",messageVo);
        }else {
            model.addAttribute("commentNotice",null);
        }

        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo=new HashMap<>();
        if (message!=null){
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data= JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVo.put("count",count );
            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unread",unread);
            model.addAttribute("likeNotice",messageVo);
        }else {
            model.addAttribute("likeNotice",null);
        }

        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo=new HashMap<>();
        if (message!=null){
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data= JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("count",count );
            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unread",unread);
            model.addAttribute("followNotice",messageVo);
        }else {
            model.addAttribute("followNotice",null);
        }

        //查询关注人发布帖子通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_PUBLISH);
        messageVo=new HashMap<>();
        if (message!=null){
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data= JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId"))); //帖子发布人
            messageVo.put("entityType",data.get("entityType")); //帖子
            messageVo.put("entityId",data.get("entityId")); //帖子id

            int count=messageService.findNoticeCount(user.getId(),TOPIC_PUBLISH);
            messageVo.put("count",count );
            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_PUBLISH);
            messageVo.put("unread",unread);
            model.addAttribute("postNotice",messageVo);
        }else {
            model.addAttribute("postNotice",null);
        }

        //查询总未读消息数量
        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    @LoginRequired
    public String getNoticeDetail(@PathVariable("topic") String topic,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                  Model model){
        User user = hostHolder.getUser();
        PageHelper.startPage(page,pageSize);
        Page<Message> noticeList = messageService.findNotices(user.getId(),topic); //包括 like,follow,comment,publish
        model.addAttribute("page", noticeList);
        String pagePath = "/notice/detail/" + topic;
        model.addAttribute("pagePath", pagePath);

        List<Map<String,Object>> noticeVoList=new ArrayList<>();
        if (noticeList!=null){
            for (Message notice: noticeList){
                Map<String,Object> map=new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content=HtmlUtils.htmlUnescape(notice.getContent());
                Map<String ,Object> data=JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }

    @RequestMapping(path = "/notice/read",method = RequestMethod.POST)
    @ResponseBody
    //一键已读
    public String read(int userId){
        messageService.oneReaded(userId);
        return CommunityUtil.getJSONString(0);
    }
}
