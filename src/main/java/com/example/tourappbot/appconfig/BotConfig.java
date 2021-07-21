package com.example.tourappbot.appconfig;

import com.example.tourappbot.TelegramBot;
import com.example.tourappbot.repostiories.QuestionRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
@Data
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUsername;
    private String botToken;

    @Bean
    public TelegramBot myTelegramBot() {
        TelegramBot telegramBot = new TelegramBot();
        telegramBot.setBotToken(botToken);
        telegramBot.setBotPath(webHookPath);
        telegramBot.setBotUsername(botUsername);
        return telegramBot;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
