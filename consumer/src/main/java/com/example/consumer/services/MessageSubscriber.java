package com.example.consumer.services;

import com.example.helper.pojos.RabbitRecord;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;


@Service
public class MessageSubscriber {

    private final Integer MAX_COUNT = 10;
    private final String ROUTING_KEY = "demo";
    private final RabbitTemplate template;

    @Autowired
    MessageSubscriber(RabbitTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = ROUTING_KEY)
    public void receive(RabbitRecord message, @Header("count") Optional<Integer> c, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {


        Integer count = c.orElse(1);

        System.out.println("\n\n\nConsumer received : " + message.content() + message.count() + "\nand count: " + count + "\n\n");

        channel.basicReject(tag, false); //terminate

//        if (count >= MAX_COUNT) {
//            channel.basicReject(tag, false); //terminate
//        } else {
//            republishMessage(ROUTING_KEY, message, ++count);
//            channel.basicAck(tag, false);
//        }

        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void republishMessage(String routingKey, Object message, Integer count) {

        template.convertAndSend(routingKey, message, m -> {
            m.getMessageProperties().setHeader("count", count);
            return m;
        });
    }

}
