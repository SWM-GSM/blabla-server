package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.domain.MemberContentDetail;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberContentDetailResponseDto {

    private Integer contextRating;
    private String userSentence;
    private String targetSentence;
    private String longFeedback;

    public static Integer getStarScoreCategory(double starScore) {
        if (starScore <= 0.3) {
            return 1;
        } else if (starScore >= 0.8) {
            return 3;
        } else {
            return 2;
        }
    }

    public static MemberContentDetailResponseDto of(MemberContentDetail memberContentDetail) {
        return MemberContentDetailResponseDto.builder()
                .contextRating(getStarScoreCategory(memberContentDetail.getContextScore()))
                .longFeedback(memberContentDetail.getLongFeedback())
                .userSentence(memberContentDetail.getUserSentence())
                .targetSentence(memberContentDetail.getContentDetail().getTargetSentence())
                .build();
    }
}
