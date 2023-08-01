package com.gsm.blabla.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticeFeedbackRequestDto {
    private String userAnswer;
    private String answer;

    @Builder
    public static PracticeFeedbackRequestDto similarityRequestDto(String userAnswer, String answer) {
        return PracticeFeedbackRequestDto.builder()
                .userAnswer(userAnswer)
                .answer(answer)
                .build();
    }
}
