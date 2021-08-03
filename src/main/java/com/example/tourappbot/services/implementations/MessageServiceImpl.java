package com.example.tourappbot.services.implementations;

import com.example.tourappbot.dto.OfferRedis;
import com.example.tourappbot.Session;
import com.example.tourappbot.TelegramBot;
import com.example.tourappbot.dto.OfferDto;
import com.example.tourappbot.dto.SessionDto;
import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.OfferRedisRepository;
import com.example.tourappbot.repostiories.SessionRedisRepository;
import com.example.tourappbot.repostiories.SessionRepository;
import com.example.tourappbot.services.interfaces.ActionService;
import com.example.tourappbot.services.interfaces.QuestionService;
import com.example.tourappbot.services.interfaces.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements com.example.tourappbot.services.interfaces.MessageService {
    ActionService actionService;
    QuestionService questionService;
    SessionRepository sessionRepostiory;
    ModelMapper modelMapper;
    RabbitService rabbitService;
    SessionService sessionService;
    SessionRedisRepository redisRepository;
    @Autowired
    TelegramBot bot;
    OfferRedisRepository offerRedisRepository;

    public MessageServiceImpl(ActionService actionService, QuestionService questionService,
                              SessionRepository sessionRepostiory,
                              ModelMapper modelMapper,
                              RabbitService rabbitService,
                              SessionService sessionService,
                              SessionRedisRepository redisRepository,
                              OfferRedisRepository offerRedisRepository) {
        this.actionService = actionService;
        this.questionService = questionService;
        this.sessionRepostiory = sessionRepostiory;
        this.modelMapper = modelMapper;
        this.rabbitService = rabbitService;
        this.sessionService = sessionService;
        this.redisRepository = redisRepository;
        this.offerRedisRepository = offerRedisRepository;
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
        if (update.getMessage().getText().equals("asd")) {
            System.out.println("asd");
        }
        SendMessage sendMessage = new SendMessage();
        Action action = null;
        long userId = update.getMessage().getFrom().getId();
        Question nextQuestion = null;
        String user_Language = "";
        Session user_question;
        Optional<Session> redisSession = redisRepository.findById(update.getMessage().getFrom().getId().toString());
        if (redisSession.isEmpty()) {
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

        List<Action> actions = actionService.getActionsByQuestion(nextQuestion);
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        initMehtod(actions, sendMessage, user_Language, user_question_map, update, nextQuestion);
        if (nextQuestion.getKey().isEmpty()) {
            createSession(update);
        }
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        return sendMessage;
    }

    public void initMehtod(List<Action> actions,
                           SendMessage sendMessage,
                           String user_Language,
                           Map<Long, Session> user_question_map, Update update,
                           Question nextQuestion) {
        Action action = null;
        if (update.getMessage().getText().equals("asd")) {
            System.out.println("asd");
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
                setSessionMap(user_Language, sendMessage, update, nextQuestion);
            } else {
                sendMessage.setText("Duzgun cavab yaz");
            }
        } else {
            if (actions.size() != 0) {
                user_question_map.get(update.getMessage().getFrom().getId()).setAction(actions.get(0));
            }
            setSessionMap(user_Language, sendMessage, update, nextQuestion);
        }
    }

    private void setSessionMap(String user_Language, SendMessage sendMessage,
                               Update update, Question nextQuestion) {
        if (update.getMessage().getText().equals("asd")) {
            System.out.println("asd");
        }
        List<Action> actions = actionService.getActionsByNextQuestion(nextQuestion);
        Optional<Session> session = redisRepository.findById(update.getMessage().getFrom().getId().toString());
        ;
        if (!session.isEmpty()) {
            if (user_Language.equals("AZ")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        session.get().getUserAnswers().put(question.getKey(), update.getMessage().getText());
                        redisRepository.save(session.get());
                    }
                }
                session.get().getUserAnswers().put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_aze());
            } else if (user_Language.equals("EN")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        session.get().getUserAnswers().put(question.getKey(), update.getMessage().getText());
                    }
                    session.get().getUserAnswers()
                            .put(question.getKey(), update.getMessage().getText());
                }
                session.get().getUserAnswers()
                        .put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_eng());
            } else if (user_Language.equals("RU")) {
                if (actions.size() > 0) {
                    Question question = actions.get(0).getQuestion();
                    if (!question.getKey().isEmpty()) {
                        session.get().getUserAnswers().put(question.getKey(), update.getMessage().getText());
                    }
                    session.get().getUserAnswers().put(question.getKey(), update.getMessage().getText());
                }
                session.get().getUserAnswers()
                        .put(nextQuestion.getKey(), null);
                sendMessage.setText(nextQuestion.getQ_ru());

            }
            redisRepository.save(session.get());
        }


    }

    @Override
    public SendMessage startMessaging(Update update, Map<Long, Session> user_map) {
        Optional<Session> sessionRedis = redisRepository.findById(update.getMessage().getFrom().getId().toString());
        if (!sessionRedis.isEmpty()) {
            System.out.println("ok");
            System.out.println(redisRepository.findAll());
        }
        long chat_id = update.getMessage().getChatId();
        String chat_id_str = String.valueOf(chat_id);
        String first_question = questionService.getQuestionByFirst().getQ_aze() + "\n" +
                questionService.getQuestionByFirst().getQ_eng() + "\n" +
                questionService.getQuestionByFirst().getQ_ru();
        SendMessage sendMessage = new SendMessage(chat_id_str, first_question);

        if (!sessionRedis.isEmpty()) {
            if (sessionRedis.get().getLang().equals("AZ")) {
                sendMessage.setText("Siz artıq başlamısınız.");
            }
            if (sessionRedis.get().getLang().equals("EN")) {
                sendMessage.setText("You have already started.");
            }
            if (sessionRedis.get().getLang().equals("RU")) {
                sendMessage.setText("Вы уже начали.");
            }
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
        if (update.getMessage().getText().equals("AZ")) {
            System.out.println("asd");
        }
        Session session = new Session();
        session.setChatId(update.getMessage().getChatId().toString());
        session.setClientId(update.getMessage().getFrom().getId().toString());
        if (update.getMessage().getText().equals("AZ")) {
            action = actionService.getActionByText("AZ");
            session.setAction(action);
            session.setLang("AZ");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_az());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
            redisRepository.save(session);
        } else if (update.getMessage().getText().equals("EN")) {
            action = actionService.getActionByText("EN");
            session.setAction(action);
            session.setLang("EN");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
            redisRepository.save(session);
        } else if (update.getMessage().getText().equals("RU")) {
            action = actionService.getActionByText("RU");
            session.setAction(action);
            session.setLang("RU");
            session.getUserAnswers().put(action.getQuestion().getKey(), action.getText_en());
            user_question_map.put(update.getMessage().getFrom().getId(), session);
            redisRepository.save(session);
        }


    }

    public SendMessage stopMessaging(Update update, Map<Long, Session> user_map) {
        List<com.example.tourappbot.models.Session> sessionList = sessionRepostiory.getSessionByClientId(update.getMessage()
                .getFrom().getId().toString());
        Optional<com.example.tourappbot.models.Session> session = sessionList.stream().filter(s -> s.isActive()).findAny();
        Optional<Session> sessionRedis = redisRepository.findById(update.getMessage().getFrom().getId().toString());
        System.out.println(sessionRedis);
        if ((!session.isEmpty()) && session.get().isActive()) {
            session.get().setActive(false);
            redisRepository.delete(sessionRedis.get());
            user_map.remove(update.getMessage().getFrom().getId());
            if (session.get().getLanguage().equals("AZ")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "Söhbəti dayandırdınız.");
            } else if (session.get().getLanguage().equals("EN")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "You stopped messaging.");
            } else if (session.get().getLanguage().equals("RU")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "Вы перестали обмениваться сообщениями.");
            }
        }
        if (!sessionRedis.isEmpty()) {
            System.out.println(sessionRedis.get());
            redisRepository.delete(sessionRedis.get());
            user_map.remove(update.getMessage().getFrom().getId());
            if (!sessionRedis.isEmpty()) {
                System.out.println("ok");
            }
            if (sessionRedis.get().getLang().equals("AZ")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "Söhbəti dayandırdınız.");
            } else if (sessionRedis.get().getLang().equals("EN")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "You stopped messaging.");
            } else if (sessionRedis.get().getLang().equals("RU")) {
                return new SendMessage(update.getMessage().getChatId().toString(), "Вы перестали обмениваться сообщениями.");
            }
        }
        return null;
    }
    @Override
    public void createSession(Update update) {
        Session session = redisRepository.findById(update.getMessage().getChatId().toString()).get();
        SessionDto sessionDto = modelMapper.map(session, SessionDto.class);
        System.out.println(sessionDto);
        com.example.tourappbot.models.Session dbSession = new com.example.tourappbot.models.Session();
        dbSession.setSessionId(session.getSessionId().toString());
        dbSession.setClientId(session.getClientId());
        dbSession.setChatId(session.getChatId());
        dbSession.setActive(true);
        dbSession.setLanguage(session.getLang());
        List<com.example.tourappbot.models.Session> sessionList = sessionRepostiory
                .getSessionByClientId(dbSession.getClientId());
        Optional<com.example.tourappbot.models.Session> existSession = sessionList.stream()
                .filter(s -> s.getSessionId().equals(dbSession.getSessionId())).findAny();
        if (!existSession.isEmpty()) {
            if (!existSession.get().isActive()) {
                sessionService.create(dbSession);
                rabbitService.sessionSender(sessionDto);
            }
        } else {
            sessionService.create(dbSession);
            rabbitService.sessionSender(sessionDto);
        }

    }

    public SendPhoto sendAgentMessage(OfferDto offerDto) {
        SendPhoto sendPhoto = new SendPhoto();
        com.example.tourappbot.models.Session session = sessionService.getSessionBySessionId(offerDto.getSessionId());
        sendPhoto.setChatId(session.getChatId());
        InputFile inputFile = new InputFile(offerDto.getImage());
        try {
            Message message = bot.execute(new SendPhoto(session.getChatId(), inputFile));
            offerDto.setMessageId(message.getMessageId());
            OfferRedis offerRedis = new OfferRedis();
            offerRedis.setOfferId(offerDto.getOfferId());
            offerRedis.setMessageId(message.getMessageId());
            offerRedisRepository.save(offerRedis);
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
        return sendPhoto;

    }
    @Override
    public void sendReply(Integer messageId,String contactInfo){
        Optional<OfferRedis> offerRedis=offerRedisRepository.findById(messageId);
        if(!offerRedis.isEmpty()){
            offerRedis.get().setContactInfo(contactInfo);
            rabbitService.replySender(offerRedis.get());
        }
    }
}
