package com.gsm.blabla.dummy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoiceRoomDto {
    private String channelName;
    private String token;
    private long expiresIn;
}
