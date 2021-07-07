package com.example.tourappbot.models;

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
    private AccessType type;
    private String text;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_Id")
    private Question question;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "next_question_id")
    private Question nextQuestion;

}
