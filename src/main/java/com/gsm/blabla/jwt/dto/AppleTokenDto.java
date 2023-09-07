package com.gsm.blabla.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppleTokenDto {

    String access_token;
    String refresh_token;
    String id_token;
}
