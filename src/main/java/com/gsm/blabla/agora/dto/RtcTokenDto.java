package com.gsm.blabla.agora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class RtcTokenDto {
    private String channelName;
    private String token;
    private Long expiresIn;
    private Long reportId;
}
