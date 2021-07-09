package com.example.tourappbot.services.implementations;

import com.example.tourappbot.TelegramBot;
import com.example.tourappbot.enums.ActionType;
import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.ActionRepository;
import com.example.tourappbot.repostiories.QuestionRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.Proxy;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MessageServiceImpl implements com.example.tourappbot.services.interfaces.MessageService {
    QuestionRepository repository;
    ActionRepository actionRepository;
    TelegramBot telegramBot;

    public MessageServiceImpl(QuestionRepository repository, ActionRepository actionRepository, TelegramBot telegramBot) {
        this.repository = repository;
        this.actionRepository = actionRepository;
        this.telegramBot = telegramBot;
    }

    @Override
    public BotApiMethod<?> sendMessage(Update update, Map<Long, Map<String, String>> user_question_map) {
        String chat_id_str;
        long chat_id;
        if (update.hasCallbackQuery()) {
            chat_id = update.getCallbackQuery().getFrom().getId();
            chat_id_str = String.valueOf(chat_id);
            if (!user_question_map.containsKey(update.getCallbackQuery().getFrom().getId())) {
                return selectLanguage(update, chat_id, chat_id_str, user_question_map);
            } else {
                return clickButton(update, user_question_map);
            }
        }
        if (update.getMessage().getText().equals("/start")) {
            return startMessaging(update, user_question_map);
        } else {
            return sendNextMessage(update, user_question_map);
        }

    }

    @Override
    public SendMessage sendNextMessage(Update update, Map<Long, Map<String, String>> user_question_map) {
        List<String> actionn;
        System.out.println(user_question_map);
        if (update.getMessage() != null) {
            actionn = new ArrayList<>(user_question_map.get(update.getMessage().getFrom().getId()).values());
        } else {
            actionn = new ArrayList<>(user_question_map.get(update.getCallbackQuery().getFrom().getId()).values());
        }
        System.out.println(actionn);
        Action action = actionRepository.getActionByText(actionn.get(actionn.size() - 1));
        Question senderQuestion = action.getNextQuestion();
        List<Action> nextAction = actionRepository.findAll().stream().filter(a -> a.getQuestion().equals(senderQuestion)).collect(Collectors.toList());
        SendMessage sendMessage = new SendMessage();
        if (nextAction.get(0).getType().equals(ActionType.BUTTON)) {
            InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (Action a : actionRepository.findAll().stream().filter(b -> b.getQuestion().equals(senderQuestion)).collect(Collectors.toList())) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(a.getText());
                inlineKeyboardButton.setCallbackData(a.getText().toLowerCase());
                rowInline.add(inlineKeyboardButton);
            }
            rowsInline.add(rowInline);
            markupLine.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupLine);

            if (senderQuestion != null) {
                String message = "";
                switch (action.getText()) {
                    case "AZ":
                        message = repository.findAll().stream().filter(q -> q.getQ_aze().equals(senderQuestion.getQ_aze()))
                                .findAny().get().getQ_aze();
                        break;
                    case "EN":
                        message = repository.findAll().stream().filter(q -> q.getQ_eng().equals(senderQuestion.getQ_eng()))
                                .findAny().get().getQ_eng();
                        break;
                    case "RU":
                        message = repository.findAll().stream().filter(q -> q.getQ_ru().equals(senderQuestion.getQ_ru()))
                                .findAny().get().getQ_ru();
                        break;
                }
                sendMessage.setChatId(update.getMessage().getChatId().toString());
                sendMessage.setText(message);
                return sendMessage;
            }
        }
        if (senderQuestion != null) {
            String message = "";
            switch (action.getText()) {
                case "AZ":
                    message = repository.findAll().stream().filter(q -> q.getQ_aze().equals(senderQuestion.getQ_aze())).findAny().get().getQ_aze();
                    break;
                case "EN":
                    message = repository.findAll().stream().filter(q -> q.getQ_eng().equals(senderQuestion.getQ_eng())).findAny().get().getQ_eng();
                    break;
                case "RU":
                    message = repository.findAll().stream().filter(q -> q.getQ_ru().equals(senderQuestion.getQ_ru())).findAny().get().getQ_ru();
                    break;
            }
            return new SendMessage(update.getMessage().getChatId().toString(), message);
        }
        return null;
    }

    @Override
    public SendMessage startMessaging(Update update, Map<Long, Map<String, String>> user_question_map) {
        long chat_id = update.getMessage().getChatId();
        String chat_id_str = String.valueOf(chat_id);
        String first_question = repository.getQuestionByIsFirst().getQ_aze() + "\n" +
                repository.getQuestionByIsFirst().getQ_eng() + "\n" +
                repository.getQuestionByIsFirst().getQ_ru();
        SendMessage sendMessage = new SendMessage(chat_id_str, first_question);
        if (user_question_map.containsKey(update.getMessage().getFrom().getId()) && user_question_map.get(update.getMessage().getFrom().getId()).equals(first_question)) {
            return new SendMessage(chat_id_str, "Siz artiq bashlamisiniz");
        }
        InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (Action a : actionRepository.findAll().stream().filter(a -> a.getQuestion().getId() == 1).collect(Collectors.toList())) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(a.getText());
            inlineKeyboardButton.setCallbackData(a.getText().toLowerCase());
            rowInline.add(inlineKeyboardButton);
        }
        rowsInline.add(rowInline);
        markupLine.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupLine);
        return sendMessage;
    }

    @Override
    public EditMessageText selectLanguage(Update update, Long chat_id, String chat_id_str, Map<Long, Map<String, String>> user_question_map) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String first_question = repository.getQuestionByIsFirst().getQ_aze() + "\n" +
                repository.getQuestionByIsFirst().getQ_eng() + "\n" +
                repository.getQuestionByIsFirst().getQ_ru();
        Map<String, String> user_map = new HashMap<>();
        Long user_id = callbackQuery.getFrom().getId();
        if (callbackQuery.getData().equals(actionRepository.getActionByText("AZ").getText().toLowerCase())) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("Azərbaycan dilini seçdiniz");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            user_map.put(first_question, actionRepository.getActionByText("AZ").getText());
            user_question_map.put(user_id, user_map);
            System.out.println(user_question_map.get(update.getCallbackQuery().getFrom().getId()).values());
            return messageText;
        } else if (callbackQuery.getData().equals(actionRepository.getActionByText("EN").getText().toLowerCase())) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("You chose English");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            user_map.put(first_question, actionRepository.getActionByText("EN").getText());
            user_question_map.put(user_id, user_map);
            return messageText;
        } else if (callbackQuery.getData().equals(actionRepository.getActionByText("RU").getText().toLowerCase())) {
            EditMessageText messageText = new EditMessageText();
            messageText.setText("Ты выбрал русский");
            messageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            messageText.setMessageId(callbackQuery.getMessage().getMessageId());
            user_map.put(first_question, actionRepository.getActionByText("RU").getText());
            user_question_map.put(user_id, user_map);
            return messageText;
        }

        return null;
    }


    @Override
    public SendMessage clickButton(Update update, Map<Long, Map<String, String>> user_question_map) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
//        Map<String,String> map=user_question_map.get()
        String first_question = repository.getQuestionByIsFirst().getQ_aze() + "\n" +
                repository.getQuestionByIsFirst().getQ_eng() + "\n" +
                repository.getQuestionByIsFirst().getQ_ru();
        Map<String, String> user_map = new HashMap<>();
        Long user_id = callbackQuery.getFrom().getId();
        return null;
    }


}
