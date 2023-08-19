package com.gsm.blabla.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppleAccountDto {

    private String iss; // team id
    private String exp; // client secret이 만료될 일시
    private String iat; // client secret이 생성된 일시
    private String sub; // client id
    private String at_hash;
    private String email;
    private Boolean email_verified;
    private Boolean is_private_email;
    private String auth_time;
    private Boolean nonce_supported;
}
