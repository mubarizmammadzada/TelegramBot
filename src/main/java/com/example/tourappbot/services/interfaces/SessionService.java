package com.example.tourappbot.services.interfaces;

import com.example.tourappbot.models.Session;

public interface SessionService {
    void create(Session session);

    void delete(Session session);

    com.example.tourappbot.models.Session getSessionBySessionId(String sessionId);
}
