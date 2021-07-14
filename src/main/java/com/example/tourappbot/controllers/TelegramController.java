package com.example.tourappbot.controllers;

import com.example.tourappbot.TelegramBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class TelegramController {
    private final TelegramBot telegramBot;
    private boolean flag = false;
    public TelegramController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateRecived(@RequestBody Update update) {
        if (flag) {
            return new SendMessage();
        }
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
