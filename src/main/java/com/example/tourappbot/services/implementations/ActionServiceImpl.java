package com.example.tourappbot.services.implementations;

import com.example.tourappbot.models.Action;
import com.example.tourappbot.models.Question;
import com.example.tourappbot.repostiories.ActionRepository;
import com.example.tourappbot.services.interfaces.ActionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {
    ActionRepository actionRepository;

    public ActionServiceImpl(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public List<Action> getActionsByQuestion(Question question) {
        return actionRepository.getActionsByQuestion(question.getId());
    }

    @Override
    public List<Action> getActionsByNextQuestion(Question question) {
        return actionRepository.getActionsByNextQuestion(question.getId());
    }

    @Override
    public Action getActionByText(String text) {
        return actionRepository.getActionByText(text);
    }

    @Override
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    @Override
    public Action getActionById(Long id) {
        return actionRepository.findById(id).get();
    }
}
