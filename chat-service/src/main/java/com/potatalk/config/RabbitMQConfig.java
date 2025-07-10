package com.potatalk.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "chat.join";
    public static final String QUEUE_NAME = "chat.join.queue";

    @Bean
    public DirectExchange chatJoinExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue chatJoinQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding chatJoinBinding() {
        return BindingBuilder.bind(chatJoinQueue()).to(chatJoinExchange()).with(QUEUE_NAME);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
