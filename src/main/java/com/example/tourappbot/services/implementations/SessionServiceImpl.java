package com.example.tourappbot.services.implementations;

import com.example.tourappbot.models.Session;
import com.example.tourappbot.repostiories.SessionRepository;
import com.example.tourappbot.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {
    SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void create(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public void delete(Session session) {
        sessionRepository.delete(session);
    }

    @Override
    public com.example.tourappbot.models.Session getSessionBySessionId(String sessionId) {
        return sessionRepository.getSessionBySessionId(sessionId);
    }
}
