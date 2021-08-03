package com.example.tourappbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto implements Serializable {
    private String clientId;
    private String chatId;
    private UUID sessionId = UUID.randomUUID();
    private String lang;
    private Long offerId;
    private Map<String, String> userAnswers = new HashMap<>();
}
