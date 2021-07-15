package com.example.tourappbot.controllers;

import com.example.tourappbot.Session;
import com.example.tourappbot.TelegramBot;
import com.example.tourappbot.services.implementations.MessageServiceImpl;
import com.example.tourappbot.services.interfaces.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@RestController
public class TelegramController {
    private final TelegramBot telegramBot;
    private final MessageServiceImpl messageService;

    public TelegramController(TelegramBot telegramBot, MessageServiceImpl messageService) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    private boolean flag = false;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (flag) {
            return new SendMessage();
        }
        return telegramBot.onWebhookUpdateReceived(update);
    }


}
