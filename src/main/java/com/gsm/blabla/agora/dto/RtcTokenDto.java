package com.gsm.blabla.agora.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RtcTokenDto {
    private String channelName;
    private String token;
    private long expiresIn;
}
