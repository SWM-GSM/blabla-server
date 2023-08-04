package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class CrewReportResponseDto {
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration koreanTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration englishTime;
    private String cloudUrl;
}
