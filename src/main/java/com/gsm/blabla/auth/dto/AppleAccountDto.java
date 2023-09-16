package com.gsm.blabla.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppleAccountDto {

    private String iss; // team id
    private String exp; // client secret이 만료될 일시
    private String iat; // client secret이 생성된 일시
    private String sub; // client id
    private String atHash;
    private String email;
    private Boolean emailVerified;
    private Boolean isPrivateEmail;
    private String authTime;
    private Boolean nonceSupported;
}
