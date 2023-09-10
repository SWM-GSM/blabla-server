package com.gsm.blabla.content.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.content.domain.MemberContentDetail;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PracticeFeedbackResponseDto {

    // TODO: 인공지능 서버 Response와 DTO 분리 고민
    private Double starScore;
    private Double contextScore;
    private String longFeedback;

    private Integer starRating;
    private Integer contextRating;
    private String userAnswer;
    private String targetSentence;

    public static Integer getStarScoreCategory(double starScore) {
        if (starScore <= 0.3) {
            return 1;
        } else if (starScore >= 0.75) {
            return 3;
        } else {
            return 2;
        }
    }
    public static PracticeFeedbackResponseDto of(MemberContentDetail memberContentDetail) {
        return PracticeFeedbackResponseDto.builder()
                .starRating(getStarScoreCategory(memberContentDetail.getStarScore()))
                .contextRating(getStarScoreCategory(memberContentDetail.getContextScore()))
                .longFeedback(memberContentDetail.getLongFeedback())
                .userAnswer(memberContentDetail.getUserAnswer())
                .targetSentence(memberContentDetail.getContentDetail().getTargetSentence())
                .build();
    }
}
