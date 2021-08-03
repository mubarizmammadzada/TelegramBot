package com.example.tourappbot.dto;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

import java.io.File;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto implements Serializable {
    private String agentNumber;
    private File image;
    private String sessionId;
    @Id
    private Long offerId;
    private Integer messageId;
    @Override
    public String toString() {
        return "OfferDto{" +
                "agentNumber='" + agentNumber + '\'' +
                ", image=" + image +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
