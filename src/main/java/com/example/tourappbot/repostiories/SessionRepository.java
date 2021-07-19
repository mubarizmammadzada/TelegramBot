package com.example.tourappbot.repostiories;

import com.example.tourappbot.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("select s from Session s where s.sessionId=:sessionId")
    Session getSessionBySessionId(String sessionId);
}
