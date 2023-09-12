package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.domain.ContentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDetailResponseDto {

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String guideSentence; // 타켓 문장
    private String targetSentence; // 모범 답안
    private String contentUrl; // 컨텐츠 URL
    private Integer startedAtSec; // 컨텐츠 시작 시간
    private Integer stoppedAtSec; // 컨텐츠 정지 시간
    private Integer endedAtSec; // 컨텐츠 종료 시간

    public static ContentDetailResponseDto contentDetailResponseDto(ContentDetail contentDetail) {
        return ContentDetailResponseDto.builder()
                .startedAtSec(contentDetail.getStartedAt().toSecondOfDay())
                .stoppedAtSec(contentDetail.getStoppedAt().toSecondOfDay())
                .endedAtSec(contentDetail.getEndedAt().toSecondOfDay())
                .guideSentence(contentDetail.getGuideSentence())
                .targetSentence(contentDetail.getTargetSentence())
                .title(contentDetail.getTitle())
                .description(contentDetail.getDescription())
                .contentUrl(contentDetail.getContentUrl())
                .build();
    }

}
