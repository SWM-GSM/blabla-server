package com.gsm.blabla.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIPracticeFeedbackRequestDto {

    private String userSentence;
    private String targetSentence;

    @Builder
    public static AIPracticeFeedbackRequestDto similarityRequestDto(String userSentence, String targetSentence) {
        return AIPracticeFeedbackRequestDto.builder()
                .userSentence(userSentence)
                .targetSentence(targetSentence)
                .build();
    }
}
