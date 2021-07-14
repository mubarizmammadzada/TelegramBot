package com.example.tourappbot.services.implementations;

import com.example.tourappbot.Session;
import com.example.tourappbot.TelegramBot;
import com.example.tourappbot.enums.ActionType;
import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.ActionRepository;
import com.example.tourappbot.repostiories.QuestionRepository;
import com.example.tourappbot.services.interfaces.ActionService;
import com.example.tourappbot.services.interfaces.QuestionService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements com.example.tourappbot.services.interfaces.MessageService {
    ActionService actionService;
    QuestionService questionService;

    public MessageServiceImpl(ActionService actionService, QuestionService questionService) {
        this.actionService = actionService;
        this.questionService = questionService;
    }

    @Override
    public BotApiMethod<?> sendMessage(Update update, Map<Long, Session> user_question_map) {
        if (update.getMessage().getText().equals("/start")) {
            return startMessaging(update, user_question_map);
        } else {
            return sendNextMessage(update, user_question_map);
        }
    }

    @Override
    public SendMessage sendNextMessage(Update update, Map<Long, Session> user_question_map) {
        Map<String, String> map = new HashMap<>();
        SendMessage sendMessage = new SendMessage();
        Action action = null;
        long userId = update.getMessage().getFrom().getId();
        Question nextQuestion = null;
        String user_Language = "";
        Session user_question;
        if (!user_question_map.containsKey(update.getMessage().getFrom().getId())) {
            setLanguage(update.getMessage().getText(), user_question_map, action, update);
        }
        user_question = user_question_map.get(userId);
        user_Language = user_question.getLang();

        if (user_question.getSessionCount() == 0) {
            nextQuestion = user_question.getAction().getNextQuestion();
            user_question.setSessionCount(1);
        } else {
            nextQuestion = user_question.getAction().getNextQuestion();
            List<Action> actionList = actionService.getActionsByQuestion(nextQuestion);
            if (actionList.size() > 1) {
                if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("AZ")) {
                    if (actionService.getActionByText(update.getMessage().getText()) == null) {
                        return new SendMessage(update.getMessage().getChatId().toString(), "Duzgun cavab yaz");
                    }
                } else if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("EN")) {
                    if (actionService.getActionByEnText(update.getMessage().getText()) == null) {
                        return new SendMessage(update.getMessage().getChatId().toString(), "Duzgun cavab yaz");
                    }

                } else if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("RU")) {
                    if (actionService.getActionByRuText(update.getMessage().getText()) == null) {
                        return new SendMessage(update.getMessage().getChatId().toString(), "Duzgun cavab yaz");
                    }
                }
                nextQuestion = actionList.get(0).getNextQuestion();
                if (update.getMessage().getText().equals("Məkanın adını qeyd elə.")) {
                    nextQuestion = actionList.get(1).getNextQuestion();
                }
            }
        }
        List<Action> actions = actionService.getActionsByQuestion(nextQuestion);
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        initMehtod(actions, sendMessage, user_Language, user_question_map, update, nextQuestion);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        return sendMessage;
    }

    public void initMehtod(List<Action> actions,
                           SendMessage sendMessage,
                           String user_Language,
                           Map<Long, Session> user_question_map, Update update,
                           Question nextQuestion) {
        Action action = null;
        if (update.getMessage().getText().equals("Recreation and walking")) {
            System.out.println("OK");
        }
        if (actions.size() > 1) {
            if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("AZ")) {
                action = actionService.getActionByText(update.getMessage().getText());
            } else if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("EN")) {
                action = actionService.getActionByEnText(update.getMessage().getText());
            } else if (user_question_map.get(update.getMessage().getFrom().getId()).getLang().equals("RU")) {
                action = actionService.getActionByRuText(update.getMessage().getText());
            }

            if (action != null) {
                List<KeyboardRow> keyboard = createRepKeyboard(actions, user_question_map.get(update.getMessage().getFrom().getId()));
                sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(keyboard, true, true, true, ""));
                user_question_map.get(update.getMessage().getFrom().getId()).setAction(action);
                setSessionMap(user_Language, user_question_map, sendMessage, update, nextQuestion);
            } else {
                sendMessage.setText("Duzgun cavab yaz");
            }
        } else {
            if (actions.size() != 0) {
                user_question_map.get(update.getMessage().getFrom().getId()).setAction(actions.get(0));
            }
            setSessionMap(user_Language, user_question_map, sendMessage, update, nextQuestion);
        }
    }

    private void setSessionMap(String user_Language, Map<Long, Session> user_question_map, SendMessage sendMessage,
                               Update update, Question nextQuestion) {
        List<Action> actions = actionService.getActionsByNextQuestion(nextQuestion);
        if (update.getMessage().getText().equals("TourApp təklif etsin.")) {
            System.out.println("OK");
        }
        if (user_Language.equals("AZ")) {
            if (actions.size() > 0) {
                Question question = actions.get(0).getQuestion();
                user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                        .put(question.getQ_aze(), update.getMessage().getText());
            }
            user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                    .put(nextQuestion.getQ_aze(), null);
            sendMessage.setText(nextQuestion.getQ_aze());
        } else if (user_Language.equals("EN")) {
            if (actions.size() > 0) {
                Question question = actions.get(0).getQuestion();
                user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                        .put(question.getQ_eng(), update.getMessage().getText());
            }
            user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                    .put(nextQuestion.getQ_eng(), null);
            sendMessage.setText(nextQuestion.getQ_eng());
        } else if (user_Language.equals("RU")) {
            if (actions.size() > 0) {
                Question question = actions.get(0).getQuestion();
                user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                        .put(question.getQ_ru(), update.getMessage().getText());
            }
            user_question_map.get(update.getMessage().getFrom().getId()).getUserAnswers()
                    .put(nextQuestion.getQ_ru(), null);
            sendMessage.setText(nextQuestion.getQ_ru());
        }
    }

    @Override
    public SendMessage startMessaging(Update update, Map<Long, Session> user_map) {
        long chat_id = update.getMessage().getChatId();
        String chat_id_str = String.valueOf(chat_id);
        String first_question = questionService.getQuestionByFirst().getQ_aze() + "\n" +
                questionService.getQuestionByFirst().getQ_eng() + "\n" +
                questionService.getQuestionByFirst().getQ_ru();
        SendMessage sendMessage = new SendMessage(chat_id_str, first_question);
        if (user_map.containsKey(update.getMessage().getFrom().getId())) {
            sendMessage.setText("Siz artiq start etmisiniz");
            return sendMessage;
        }
        List<KeyboardRow> keyboard = createRepKeyboard(actionService.getAllActions().stream().filter(a -> a.getQuestion().getId() == 1).collect(Collectors.toList()), null);
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(keyboard, true, true, true, ""));
        return sendMessage;
    }

    public List<KeyboardRow> createRepKeyboard(List<Action> actionTranslates, Session session) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (Action text : actionTranslates) {
            KeyboardRow row = new KeyboardRow();
            if (session != null) {
                if (session.getLang().equals("AZ")) {
                    row.add(new KeyboardButton(text.getText_az()));
                } else if (session.getLang().equals("EN")) {
                    row.add(new KeyboardButton(text.getText_en()));
                } else if (session.getLang().equals("RU")) {
                    row.add(new KeyboardButton(text.getText_ru()));
                }
            } else {
                row.add(new KeyboardButton(text.getText_az()));
            }

            keyboard.add(row);
        }
        return keyboard;
    }

    public void setLanguage(String language, Map<Long, Session> user_question_map, Action action, Update update) {
        Session session = new Session();
        if (update.getMessage().getText().equals("AZ")) {
            action = actionService.getActionByText("AZ");
            session.setAction(action);
            session.setLang("AZ");
            session.getUserAnswers().put(action.getQuestion().getQ_aze(), action.getText_az());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        } else if (update.getMessage().getText().equals("EN")) {
            action = actionService.getActionByText("EN");
            session.setAction(action);
            session.setLang("EN");
            session.getUserAnswers().put(action.getQuestion().getQ_eng(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        } else if (update.getMessage().getText().equals("RU")) {
            action = actionService.getActionByText("RU");
            session.setAction(action);
            session.setLang("RU");
            session.getUserAnswers().put(action.getQuestion().getQ_ru(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        }
    }
}
