package com.example.tourappbot.services.implementations;

import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.QuestionRepository;
import com.example.tourappbot.services.interfaces.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).get();
    }

    @Override
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionByFirst() {
        return questionRepository.getQuestionByIsFirst();
    }
}
