package com.gsm.blabla.content.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIPracticeFeedbackResponseDto {

    private Double contextScore;
    private String longFeedback;
}
