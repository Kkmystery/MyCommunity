package com.kk.community.event;

import com.alibaba.fastjson.JSONObject;
import com.kk.community.util.Event;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : K k
 * @date : 9:57 2020/5/5
 */
@Component
public class EventProducer {
    /*@Autowired
    private KafkaTemplate kafkaTemplate;*/
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Value("${community.rabbitmq.exchange}")
    String exchange;

    //处理事件
    public void fireEvent(Event event){
        //将事件发布到指定的主题
        rabbitTemplate.convertAndSend(exchange,event.getTopic(), event);
    }
}
