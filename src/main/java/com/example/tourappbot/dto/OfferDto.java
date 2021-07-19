package com.example.tourappbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;

@Getter
@Setter
public class OfferDto implements Serializable {
    private String agentNumber;
    private File image;
    private String sessionId;

    @Override
    public String toString() {
        return "OfferDto{" +
                "agentNumber='" + agentNumber + '\'' +
                ", image=" + image +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
