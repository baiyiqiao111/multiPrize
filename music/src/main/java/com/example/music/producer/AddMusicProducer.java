package com.example.music.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddMusicProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    public void send(String topic,int id){
        kafkaTemplate.send(topic, String.valueOf(id));
        System.out.println("消息发送成功"+id);
    }
}
