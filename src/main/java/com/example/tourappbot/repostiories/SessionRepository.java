package com.example.tourappbot.repostiories;

import com.example.tourappbot.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("select s from Session s where s.sessionId=:sessionId")
    com.example.tourappbot.models.Session getSessionBySessionId(String sessionId);
    @Query("select s from Session s where s.clientId=:clientId")
    List<Session> getSessionByClientId(String clientId);
}
