package com.example.tourappbot.services.interfaces;

import com.example.tourappbot.Session;
import com.example.tourappbot.dto.OfferDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.Map;

public interface MessageService {
    SendMessage startMessaging(Update update, Map<Long, Session> session);

    PartialBotApiMethod<Message> sendMessage(Update update, Map<Long, Session> session, @Nullable OfferDto offerDto) throws JsonProcessingException;

    SendMessage sendNextMessage(Update update, Map<Long, Session> session);
}
