package com.example.tourappbot.repostiories;

import com.example.tourappbot.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("select q from Question q where q.isFirst=true")
    Question getQuestionByIsFirst();
    @Query("select  q from Question q where q.q_aze=:aze_word")
    Question getQuestionByQ_aze(String aze_word);

}
