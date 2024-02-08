package com.example.producer.controllers;

import com.example.helper.pojos.RabbitRecord;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagePublisher {


    private final RabbitTemplate rabbitTemplate;
    private final CachingConnectionFactory connectionFactory;

    public MessagePublisher(RabbitTemplate rabbitTemplate,
                            CachingConnectionFactory connectionFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionFactory = connectionFactory;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody String message) {


        try (Connection connection = connectionFactory.createConnection();
             Channel channel = connection.createChannel(false)) {

            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive("dl.queue");
            int messageCount = declareOk.getMessageCount();

            System.out.println("Messages in queue: " + messageCount);
        } catch (Exception ignored) {}


        rabbitTemplate.convertAndSend("demo", new RabbitRecord(message, 69), m -> {
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });
    }
}
