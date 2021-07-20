package com.example.tourappbot;

import com.example.tourappbot.repostiories.QuestionRepository;
import com.example.tourappbot.services.interfaces.MessageService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
//@Component
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;
    @Autowired
    QuestionRepository repository;
    @Autowired
    MessageService service;
    Map<Long, Session> user_question_map = new HashMap<>();

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        if (update.getMessage() != null && update.getMessage().hasText()) {
            return (BotApiMethod<?>) service.sendMessage(update, user_question_map,null);
        }
        return null;

    }
}
