package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewReportAnalysisResponseDto {
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration koreanTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration englishTime;
    private String cloudUrl;
}
