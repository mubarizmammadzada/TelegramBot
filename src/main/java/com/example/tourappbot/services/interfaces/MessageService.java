package com.example.tourappbot.services.interfaces;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public interface MessageService {
    SendMessage startMessaging(Update update, Map<Long,Map<String,String>> user_question_map);
    EditMessageText selectLanguage(Update update, Long chat_id, String chat_id_str,Map<Long, Map<String,String>> user_question_map);
    SendMessage clickButton(Update update,Map<Long,Map<String,String>> user_question_map);
    BotApiMethod<?> sendMessage(Update update, Map<Long,Map<String,String>> user_question_map);
    SendMessage sendNextMessage(Update update, Map<Long,Map<String,String>> user_question_map);
}
