package com.example.producer.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@EnableRabbit
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;


    @Bean
    Exchange dlxExchange() {
        return ExchangeBuilder.directExchange("dlx.exchange").durable(true).build();
    }

    @Bean
    Queue dlQueue() {
        return QueueBuilder.durable("dl.queue").build();
    }

    @Bean
    Binding dlBinding(Queue dlQueue, Exchange dlxExchange) {
        return BindingBuilder.bind(dlQueue).to(dlxExchange).with("dl.routing.key").noargs();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable("demo")
                           .withArgument("x-dead-letter-exchange", "dlx.exchange")
                           .withArgument("x-dead-letter-routing-key", "dl.routing.key")
                           .build();
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitHost);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return connectionFactory;
    }

    @Bean
    @Primary
    RabbitTemplate rabbitTemplate(final ConnectionFactory factory) {
        final RabbitTemplate template = new RabbitTemplate(factory);
        template.setConfirmCallback(((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("message confirmed");
            } else {
                System.out.println("message  not confirmed: " + cause);
            }
        }));
        template.setMessageConverter(messageConverter());
        return template;
    }

}
