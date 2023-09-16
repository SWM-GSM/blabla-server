package com.gsm.blabla.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessageRequestDto {

    private String targetToken;
    private String title;
    private String body;
}
