package com.example.tourappbot.services.interfaces;

import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ActionService {
    List<Action> getActionsByQuestion(Question question);
    List<Action> getActionsByNextQuestion(Question question);

    Action getActionByText(String text);

    List<Action> getAllActions();

    Action getActionById(Long id);

}
