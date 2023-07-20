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
    private Long id;
    private String contentUrl; // 컨텐츠 URL
    private int level; // 컨텐츠 레벨
    private String sentence; // 타켓 문장
    private String answer; // 모범 답안
    private String language; // 언어
    private String topic; // 컨텐츠 주제
    private String title; // 컨텐츠 제목

    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private boolean isCompleted; // 컨텐츠 완료 여부

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

    public static List<ContentResponseDto> contentListResponse(List<Content> contents, MemberContentRepository memberContentRepository) {
        return contents.stream()
                .map(content -> {
                    return ContentResponseDto.builder()
                            .id(content.getId())
                            .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                            .level(content.getLevel())
                            .topic(content.getTopic())
                            .title(content.getTitle())
                            .isCompleted(memberContentRepository.findByContentId(content.getId()).isPresent())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
