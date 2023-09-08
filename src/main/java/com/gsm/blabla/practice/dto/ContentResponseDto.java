package com.gsm.blabla.practice.dto;

import com.gsm.blabla.practice.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponseDto {

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명

    private String guideSentence; // 타켓 문장
    private String targetSentence; // 모범 답안

    private String youtubeId; // 컨텐츠 유튜브 ID

    private Long startedAtSec; // 컨텐츠 시작 시간
    private Long stoppedAtSec; // 컨텐츠 정지 시간
    private Long endedAtSec; // 컨텐츠 종료 시간

    public static ContentResponseDto contentResponse(Content content) {

        return ContentResponseDto.builder()
                .startedAtSec(content.getStartedAt().getSeconds())
                .stoppedAtSec(content.getStoppedAt().getSeconds())
                .endedAtSec(content.getEndedAt().getSeconds())
                .guideSentence(content.getGuideSentence())
                .targetSentence(content.getTargetSentence())
                .title(content.getTitle())
                .description(content.getDescription())
                .build();
    }
}
