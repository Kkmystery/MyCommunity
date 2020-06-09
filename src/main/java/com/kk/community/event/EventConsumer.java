package com.kk.community.event;

import com.alibaba.fastjson.JSONObject;
import com.kk.community.entity.DiscussPost;
import com.kk.community.entity.Message;
import com.kk.community.service.DiscussPostService;
import com.kk.community.service.ElasticsearchService;
import com.kk.community.service.FollowService;
import com.kk.community.service.MessageService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author : K k
 * @date : 9:59 2020/5/5
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private FollowService followService;

    //一对一
    @RabbitListener(queues = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(Event event){
        if(event==null){
            logger.error("消息格式错误");
            return;
        }
        //Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        /*if (event==null){
            logger.error("消息格式错误");
            return;
        }*/

        //构造通知
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        Map<String ,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        //将消息额外的数据也放入content中封装起来
        if (!event.getData().isEmpty()){
            for (Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        //以json格式存放内容
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //一对多
    //消费发帖消息
    @RabbitListener(queues = {TOPIC_PUBLISH})
    public void handlePublishMessage(Event event){
        if (event==null){
            logger.error("消息内容为空");
            return;
        }
        //Event event=JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        DiscussPost discussPost=discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);

        //将发帖的消息转发给粉丝们
        List<Integer> followersId = followService.findFollowersId(event.getUserId());
        if(followersId!=null&&followersId.size()>0){
            for(Integer id:followersId){
                //构造通知
                Message message=new Message();
                message.setFromId(SYSTEM_USER_ID); //系统
                message.setToId(id);    //粉丝收到帖子消息
                message.setConversationId(event.getTopic()); //publish
                message.setStatus(0); //未删除
                message.setCreateTime(new Date()); //日期

                Map<String ,Object> content=new HashMap<>();
                content.put("userId",event.getUserId());
                content.put("entityType",event.getEntityType());
                content.put("entityId",event.getEntityId());
                //将消息额外的数据也放入content中封装起来
                if (!event.getData().isEmpty()){
                    for (Map.Entry<String,Object> entry:event.getData().entrySet()){
                        content.put(entry.getKey(),entry.getValue());
                    }
                }
                message.setContent(JSONObject.toJSONString(content));
                messageService.addMessage(message);
            }
        }
    }

    //消费删帖消息
    @RabbitListener(queues = {TOPIC_DELETE})
    public void handleDeleteMessage(Event event){
        if (event==null){
            logger.error("消息内容为空");
            return;
        }
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        //DiscussPost discussPost=discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }
}
