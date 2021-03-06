package com.example.tourappbot.models;

import com.example.tourappbot.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Enumerated(EnumType.STRING)
    private ActionType type;
    private String text_az;
    private String text_en;
    private String text_ru;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_Id")
    private Question question;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "next_question_id")
    private Question nextQuestion;

}
