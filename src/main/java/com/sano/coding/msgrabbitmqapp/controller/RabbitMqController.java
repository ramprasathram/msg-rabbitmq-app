package com.sano.coding.msgrabbitmqapp.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class RabbitMqController {
    static Logger logger = LoggerFactory.getLogger(RabbitMqController.class);

    @Autowired
    private RabbitTemplate customRabbitTemplate;

    @RequestMapping(value = "/sample/{user}/{type}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String sampleMethod(@PathVariable String user, @PathVariable String type) {
        String requestJson = null;
        String responseString = null;
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("username", user);
        data.put("type", type);
        requestJson = new Gson().toJson(data);
        logger.info("requestJson -- "+ requestJson);
        Message message = MessageBuilder.withBody(requestJson.getBytes()).setContentType(MediaType.APPLICATION_JSON_VALUE).build();
        Message result = customRabbitTemplate.sendAndReceive("eName", "qName", message);
        responseString = new String(result.getBody());
        logger.info("responseString -- "+ responseString);
        return responseString;
    }
}
