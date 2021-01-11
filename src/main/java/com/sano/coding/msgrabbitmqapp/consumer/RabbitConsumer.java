package com.sano.coding.msgrabbitmqapp.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {
    static Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    @RabbitListener(queues = "qName", containerFactory = "rabbitListenerContainerFactory")
    public Message lineInquiryResponse(Message msg) {
        byte[] body = msg.getBody();
        String result = new String(body);
        logger.info("result -- "+ result);
        return MessageBuilder.withBody(result.getBytes()).setContentType(MediaType.APPLICATION_JSON_VALUE).build();
    }
}
