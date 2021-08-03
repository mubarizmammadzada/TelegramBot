package com.example.tourappbot.services.implementations;

import com.example.tourappbot.appconfig.MessagingConfig;
import com.example.tourappbot.dto.OfferRedis;
import com.example.tourappbot.dto.SessionDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {
    RabbitTemplate template;

    public RabbitService(RabbitTemplate template) {
        this.template = template;
    }

    public void sessionSender(SessionDto sessionDto) {
        template.convertAndSend(MessagingConfig.QUEUE, sessionDto);
    }

    public void replySender(OfferRedis reply) {
        template.convertAndSend(MessagingConfig.REPLY_QUEUE, reply);
    }
}
