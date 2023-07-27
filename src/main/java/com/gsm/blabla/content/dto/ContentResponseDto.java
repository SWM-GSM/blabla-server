package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.dao.MemberContentRepository;
import com.gsm.blabla.content.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponseDto {
    private String contentUrl; // 컨텐츠 URL
    private Long level; // 컨텐츠 레벨
    private String sentence; // 타켓 문장
    private String answer; // 모범 답안
    private String topic; // 컨텐츠 주제
    private String title; // 컨텐츠 제목

    public static ContentResponseDto contentResponse(Content content) {

        return ContentResponseDto.builder()
                .contentUrl(content.getContentUrl())
                .level(content.getLevel())
                .sentence(content.getSentence())
                .answer(content.getAnswer())
                .topic(content.getTopic())
                .title(content.getTitle())
                .build();
    }
}
