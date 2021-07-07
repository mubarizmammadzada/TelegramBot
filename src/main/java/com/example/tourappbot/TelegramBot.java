package com.example.tourappbot;

import com.example.tourappbot.repostiories.QuestionRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;
    @Autowired
    QuestionRepository repository;
    Map<Long, String> user_question_map = new HashMap<>();

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String chat_id_str = String.valueOf(chat_id);
            if (update.getMessage().getText().equals("/start")) {
                SendMessage sendMessage = startMessaging(update);
                System.out.println(sendMessage.getReplyMarkup());
                return sendMessage;
            }
        }
        return null;


    }

    public SendMessage startMessaging(Update update) {
        long chat_id = update.getMessage().getChatId();
        String chat_id_str = String.valueOf(chat_id);
        String first_question = repository.getQuestionByIsFirst().getName();

        SendMessage sendMessage = new SendMessage(chat_id_str, first_question);
        if (user_question_map.values().contains(first_question)) {
            return new SendMessage(chat_id_str, "starti basma yibal");
        }
        System.out.println(update.getMyChatMember());
        user_question_map.put(chat_id, first_question);
        InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton.setText("AZ");
        inlineKeyboardButton1.setText("EN");
        inlineKeyboardButton2.setText("RU");
        inlineKeyboardButton.setCallbackData("language_selection");
        inlineKeyboardButton1.setCallbackData("language_selection");
        inlineKeyboardButton2.setCallbackData("language_selection");
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        rowsInline.add(rowInline);
        markupLine.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupLine);
        System.out.println(update.getMessage().getText());
        return sendMessage;
    }


}
