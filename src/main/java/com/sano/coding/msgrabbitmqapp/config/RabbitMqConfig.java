package com.sano.coding.msgrabbitmqapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Value("${spring.application.dlqExchange}")
    private String dlqExchange;

    @Value("${spring.application.dlqueue}")
    private String dlqueue;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlqExchange);
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(dlqueue).build();
    }

    @Bean
    Binding DLQbinding() {
        return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with(dlqueue);
    }

    @Bean
    public Queue customQueue() {
        return QueueBuilder.durable("qName")
                .withArgument("x-dead-letter-exchange", dlqExchange).withArgument("x-dead-letter-routing-key", dlqueue)
                .build();
    }

    @Bean
    public DirectExchange customExchange() {
        return new DirectExchange("eName");
    }

    @Bean
    Binding customBinding() {
        return BindingBuilder.bind(customQueue()).to(customExchange()).with("qName");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "ConnectionFactory")
    @Primary
    public ConnectionFactory rabbitMQConnectionFactory() {
        logger.info("host -- "+ host+ " port --"+ port+ " username --"+ username+ " password --"+ password);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(Integer.parseInt(port));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setConnectionTimeout(30000);
        return connectionFactory;
    }

    @Bean()
    RabbitAdmin lineInquiryRabbitAdmin(){
        return new RabbitAdmin(rabbitMQConnectionFactory());
    }

    @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory (){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(20);
        factory.setMaxConcurrentConsumers(25);
        factory.setPrefetchCount(1);
        factory.setReceiveTimeout((long) 50000);
        factory.setConnectionFactory(rabbitMQConnectionFactory());
        return factory;
    }

    @Bean()
    public RabbitTemplate lineInquiryCustomRabbitTemplate() {
        RabbitTemplate lineInquiryRabbitTemplate = new RabbitTemplate();
        lineInquiryRabbitTemplate.setReplyTimeout(30000L);
        lineInquiryRabbitTemplate.setMessageConverter(jsonMessageConverter());
        lineInquiryRabbitTemplate.setConnectionFactory(rabbitMQConnectionFactory());
        return lineInquiryRabbitTemplate;
    }

}
