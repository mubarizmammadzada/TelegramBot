package com.example.tourappbot.consumer;

import com.example.tourappbot.dto.OfferDto;
import com.example.tourappbot.models.Session;
import com.example.tourappbot.services.implementations.MessageServiceImpl;
import com.example.tourappbot.services.interfaces.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BotConsumer {
    SessionService sessionService;
    MessageServiceImpl messageService;

    public BotConsumer(SessionService sessionService, MessageServiceImpl messageService) {
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    @RabbitListener(queues = "rabbit_queue")
    public void myMethod(OfferDto offerDto) throws JsonProcessingException {
        Session session = sessionService.getSessionBySessionId(offerDto.getSessionId());
        if (session != null) {
            messageService.sendMessage(null, null, offerDto);
        }
        System.out.println(session);
    }
}
