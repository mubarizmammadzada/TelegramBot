package com.example.tourappbot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String chatId;
    private String sessionId;
    private String clientId;
    private boolean isActive;

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
