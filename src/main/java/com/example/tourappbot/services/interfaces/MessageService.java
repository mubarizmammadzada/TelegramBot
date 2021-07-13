package com.example.tourappbot.services.interfaces;

import com.example.tourappbot.Session;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public interface MessageService {
    SendMessage startMessaging(Update update, Map<Long, Session> session);

    BotApiMethod<?> sendMessage(Update update, Map<Long, Session> session);

    SendMessage sendNextMessage(Update update, Map<Long, Session> session);
}
