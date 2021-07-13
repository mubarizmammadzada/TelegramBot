package com.example.tourappbot;

import com.example.tourappbot.models.Action;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Session {
    @NonNull
    private String clientId;
    @NonNull
    private String chatId;
    private UUID sessionId = UUID.randomUUID();
    private String lang;
    private Map<String, String> userAnswers = new HashMap<>();
    private Action action;
    private int sessionCount;
}
