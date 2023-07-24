package com.gsm.blabla.dummy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeywordDto {
    private String emoji;
    private String name;
}