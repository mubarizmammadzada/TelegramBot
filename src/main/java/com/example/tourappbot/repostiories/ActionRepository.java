package com.example.tourappbot.repostiories;

import com.example.tourappbot.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    @Query("select a from Action a where a.text=:text")
    Action getActionByText(String text);

    @Query("select a from Action a where a.question.id=:id")
    List<Action> getActionsByQuestion(Long id);

    @Query("select a from Action a where a.nextQuestion.id=:id")
    List<Action> getActionsByNextQuestion(Long id);

}
