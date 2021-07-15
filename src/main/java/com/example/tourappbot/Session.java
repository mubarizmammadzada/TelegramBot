package com.example.tourappbot;

import com.example.tourappbot.models.Action;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@RedisHash("sessions")
public class Session {
    @NonNull
    private String clientId;
    @Id
    @NonNull
    private String chatId;
    private UUID sessionId = UUID.randomUUID();
    private String lang;
    private Map<String, String> userAnswers = new HashMap<>();
    private Action action;
    private int sessionCount;
}
