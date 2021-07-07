package com.example.tourappbot;

import com.example.tourappbot.repostiories.QuestionRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
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
        long chat_id;
        String chat_id_str;
        if (update.getMessage() != null && update.getMessage().hasText()) {
            chat_id = update.getMessage().getChatId();
            chat_id_str = String.valueOf(chat_id);
            if (update.getMessage().getText().equals("/start")) {
                SendMessage sendMessage = startMessaging(update);
                return sendMessage;
            }

        }
        if (update.hasCallbackQuery()) {
            chat_id = update.getCallbackQuery().getFrom().getId();
            chat_id_str = String.valueOf(chat_id);
            return selectLanguage(update, chat_id, chat_id_str);
        }
        return null;


    }

    public SendMessage startMessaging(Update update) {
        long chat_id = update.getMessage().getChatId();
        String chat_id_str = String.valueOf(chat_id);
        String first_question = repository.getQuestionByIsFirst().getQ_aze() + "\n" +
                repository.getQuestionByIsFirst().getQ_eng() + "\n" +
                repository.getQuestionByIsFirst().getQ_ru();
        SendMessage sendMessage = new SendMessage(chat_id_str, first_question);
        if (user_question_map.containsKey(chat_id) && user_question_map.get(chat_id).equals(first_question)) {
            return new SendMessage(chat_id_str, "Sikdir");
        }
//        if (user_question_map.values().contains(first_question)) {
//            return new SendMessage(chat_id_str, "starti basma yibal");
//        }

        user_question_map.put(chat_id, first_question);
        System.out.println(user_question_map);
        InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton.setText("AZ");
        inlineKeyboardButton1.setText("EN");
        inlineKeyboardButton2.setText("RU");
        inlineKeyboardButton.setCallbackData("aze");
        inlineKeyboardButton1.setCallbackData("en");
        inlineKeyboardButton2.setCallbackData("ru");
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        rowsInline.add(rowInline);
        markupLine.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupLine);
        return sendMessage;
    }

    public EditMessageText selectLanguage(Update update, Long chat_id, String chat_id_str) {

        CallbackQuery callbackQuery = update.getCallbackQuery();

        if (callbackQuery.getData().equals("aze")) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("Azərbaycan dilini seçdiniz");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return messageText;
        } else if (callbackQuery.getData().equals("en")) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("You chose English");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return messageText;
        } else if (callbackQuery.getData().equals("ru")) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("Ты выбрал русский");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return messageText;
        }

        return null;
    }

}
