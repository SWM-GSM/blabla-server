package com.gsm.blabla.practice.dto;

import com.gsm.blabla.practice.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponseDto {
    private String contentUrl; // 컨텐츠 URL
    private String sentence; // 타켓 문장
    private String answer; // 모범 답안
    private String topic; // 컨텐츠 주제
    private String contentName; // 컨텐츠 제목

    public static ContentResponseDto contentResponse(Content content) {

        return ContentResponseDto.builder()
                .contentUrl(content.getContentUrl())
                .sentence(content.getSentence())
                .answer(content.getAnswer())
                .topic(content.getTopic())
                .contentName(content.getContentName())
                .build();
    }
}
