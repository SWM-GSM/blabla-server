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
    private String contentUrl; // 컨텐츠 URL
    private Long startedAtSec; // 컨텐츠 시작 시간
    private Long stoppedAtSec; // 컨텐츠 정지 시간
    private Long endAtSec; // 컨텐츠 종료 시간
    private String sentence; // 타켓 문장
    private String answer; // 모범 답안
    private String topic; // 컨텐츠 주제
    private String contentName; // 컨텐츠 제목
    private String genre; // 컨텐츠 장르

    public static ContentResponseDto contentResponse(Content content) {

        return ContentResponseDto.builder()
                .contentUrl(content.getContentUrl())
                .startedAtSec(content.getStartedAt().getSeconds())
                .stoppedAtSec(content.getStoppedAt().getSeconds())
                .endAtSec(content.getEndAt().getSeconds())
                .sentence(content.getSentence())
                .answer(content.getAnswer())
                .topic(content.getTopic())
                .contentName(content.getContentName())
                .genre(content.getGenre())
                .build();
    }
}
