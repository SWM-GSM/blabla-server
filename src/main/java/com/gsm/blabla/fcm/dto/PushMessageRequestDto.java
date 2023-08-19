package com.gsm.blabla.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PushMessageRequestDto {

    private boolean validateOnly;
    private Message message;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Message {
        private Notification notification;
        private String token;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
    }
}
