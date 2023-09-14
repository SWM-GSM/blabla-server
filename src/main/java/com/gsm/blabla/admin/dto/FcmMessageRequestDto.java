package com.gsm.blabla.admin.dto;

import lombok.Getter;

@Getter
public class FcmMessageRequestDto {

    private String targetToken;
    private String title;
    private String body;
}
