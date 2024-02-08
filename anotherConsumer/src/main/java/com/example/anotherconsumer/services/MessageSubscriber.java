package com.example.anotherconsumer.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageSubscriber {

    @RabbitListener(queues = "demo")
    public void receive(String message) {
        System.out.println("\n\nAnother consumer received: " + message + "\n\n");
    }

}
