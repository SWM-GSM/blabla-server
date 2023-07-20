package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponseDto {
    private Long id;
    private String contentUrl; // 컨텐츠 URL
    private int level; // 컨텐츠 레벨
    private String sentence; // 타켓 문장
    private String answer; // 모범 답안
    private String language; // 언어
    private String topic; // 컨텐츠 주제
    private String title; // 컨텐츠 제목

    public static ContentResponseDto contentResponse(Content content) {

        return ContentResponseDto.builder()
                .id(content.getId())
                .contentUrl(content.getContentUrl())
                .level(content.getLevel())
                .sentence(content.getSentence())
                .answer(content.getAnswer())
                .language(content.getLanguage())
                .topic(content.getTopic())
                .title(content.getTitle())
                .build();
    }
}
