package com.example.tourappbot.services.implementations;

import com.example.tourappbot.Session;
import com.example.tourappbot.dto.OfferDto;
import com.example.tourappbot.dto.SessionDto;
import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.SessionRedisRepository;
import com.example.tourappbot.services.interfaces.ActionService;
import com.example.tourappbot.services.interfaces.QuestionService;
import com.example.tourappbot.services.interfaces.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements com.example.tourappbot.services.interfaces.MessageService {
    ActionService actionService;
    QuestionService questionService;
    SessionRedisRepository sessionRepostiory;
    ModelMapper modelMapper;
    RabbitService rabbitService;
    SessionService sessionService;

    public MessageServiceImpl(ActionService actionService, QuestionService questionService, SessionRedisRepository sessionRepostiory, ModelMapper modelMapper, RabbitService rabbitService, SessionService sessionService) {
        this.actionService = actionService;
        this.questionService = questionService;
        this.sessionRepostiory = sessionRepostiory;
        this.modelMapper = modelMapper;
        this.rabbitService = rabbitService;
        this.sessionService = sessionService;
    }

    @Override
    public PartialBotApiMethod<Message> sendMessage(Update update, Map<Long, Session> user_question_map
            , @Nullable OfferDto offerDto) throws JsonProcessingException {
        if (offerDto != null) {
            return sendAgentMessage(offerDto);
        }
        if (update.getMessage().getText().equals("/start")) {
            return startMessaging(update, user_question_map);
        } else if (update.getMessage().getText().equals("/stop")) {
            return stopMessaging(update, user_question_map);
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
        if (user_question == null) {
            return new SendMessage(update.getMessage().getChatId().toString(), "Zehmet olmasa /start duymesine basin.");
        }
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
        if (nextQuestion.getKey().isEmpty()) {
            System.out.println(generateJson(update, user_question_map));
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
        Session session = sessionRepostiory.findById(update.getMessage().getFrom().getId().toString()).get();
        ;
        if (session != null) {
            if (user_Language.equals("AZ")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        if (!update.getMessage().getText().equals("AZ")) {
                            System.out.println("ok");
                        }
                        session.getUserAnswers().put(question.getKey(), update.getMessage().getText());
                        sessionRepostiory.save(session);
                    }
                }
                session.getUserAnswers().put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_aze());
            } else if (user_Language.equals("EN")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        session.getUserAnswers().put(question.getKey(), update.getMessage().getText());
                    }
                    session.getUserAnswers()
                            .put(question.getKey(), update.getMessage().getText());
                }
                session.getUserAnswers()
                        .put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_eng());
            } else if (user_Language.equals("RU")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        session.getUserAnswers().put(question.getKey(), update.getMessage().getText());
                    }
                    session.getUserAnswers().put(question.getKey(), update.getMessage().getText());
                }
                session.getUserAnswers()
                        .put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_ru());

            }
            sessionRepostiory.save(session);
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
            if (user_map.get(update.getMessage().getFrom().getId()).getLang().equals("AZ")) {
                sendMessage.setText("Siz artıq başlamısınız.");
            }
            if (user_map.get(update.getMessage().getFrom().getId()).getLang().equals("EN")) {
                sendMessage.setText("You have already started.");
            }
            if (user_map.get(update.getMessage().getFrom().getId()).getLang().equals("ru")) {
                sendMessage.setText("Вы уже начали.");
            }
            sendMessage.setText("Siz artıq başlamısınız.");
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
        session.setChatId(update.getMessage().getChatId().toString());
        session.setClientId(update.getMessage().getFrom().getId().toString());
        if (update.getMessage().getText().equals("AZ")) {
            action = actionService.getActionByText("AZ");
            session.setAction(action);
            session.setLang("AZ");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_az());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        } else if (update.getMessage().getText().equals("EN")) {
            action = actionService.getActionByText("EN");
            session.setAction(action);
            session.setLang("EN");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        } else if (update.getMessage().getText().equals("RU")) {
            action = actionService.getActionByText("RU");
            session.setAction(action);
            session.setLang("RU");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
        }
        sessionRepostiory.save(session);

    }

    public SendMessage stopMessaging(Update update, Map<Long, Session> user_map) {
        if (user_map.containsKey(update.getMessage().getFrom().getId())) {
            sessionRepostiory.delete(user_map.get(update.getMessage().getFrom().getId()));
            user_map.remove(update.getMessage().getFrom().getId());
            return new SendMessage(update.getMessage().getChatId().toString(), "Stop etdiniz");
        }
        return null;
    }

    public SessionDto generateJson(Update update, Map<Long, Session> user_map) {
        Session session = sessionRepostiory.findById(update.getMessage().getChatId().toString()).get();
        SessionDto sessionDto = modelMapper.map(session, SessionDto.class);
        System.out.println(sessionDto);
        com.example.tourappbot.models.Session dbSession = new com.example.tourappbot.models.Session();
        dbSession.setSessionId(session.getSessionId().toString());
        dbSession.setClientId(session.getClientId());
        dbSession.setChatId(session.getChatId());
        dbSession.setActive(true);
        sessionService.create(dbSession);
        rabbitService.sessionSender(sessionDto);
        return sessionDto;
    }

    public SendPhoto sendAgentMessage(OfferDto offerDto) {
        SendPhoto sendPhoto=new SendPhoto();
        com.example.tourappbot.models.Session session = sessionService.getSessionBySessionId(offerDto.getSessionId());
        sendPhoto.setChatId(session.getChatId());
        InputFile inputFile = new InputFile(offerDto.getImage());
        sendPhoto.setPhoto(inputFile);
        return sendPhoto;
    }
}
