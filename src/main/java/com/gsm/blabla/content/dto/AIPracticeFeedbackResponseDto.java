package com.gsm.blabla.content.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AIPracticeFeedbackResponseDto {

    private Double contextScore;
    private String longFeedback;
}
