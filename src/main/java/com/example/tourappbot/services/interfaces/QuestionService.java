package com.example.tourappbot.services.interfaces;

import com.example.tourappbot.models.Question;

import java.util.List;

public interface QuestionService {
    Question getQuestionById(Long id);

    List<Question> getQuestions();

    Question getQuestionByFirst();

}
