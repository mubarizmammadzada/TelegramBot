package com.example.tourappbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@SpringBootApplication
@EnableCaching
public class TourAppBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourAppBotApplication.class, args);
    }


}
