package com.gsm.blabla.practice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.practice.dao.MemberContentRepository;
import com.gsm.blabla.practice.domain.Content;
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
public class ContentViewResponseDto {
    private Long id;
    private String contentName; // 컨텐츠 제목
    private String genre; // 컨텐츠 카테고리
    private String topic; // 컨텐츠 주제

    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private Boolean isCompleted; // 컨텐츠 완료 여부

    public static ContentViewResponseDto contentViewResponse(Content content, Long memberId, MemberContentRepository memberContentRepository) {

        return ContentViewResponseDto.builder()
                .id(content.getId())
                .contentName(content.getContentName())
                .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                .topic(content.getTopic())
                .isCompleted(memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isPresent())
                .genre(content.getGenre())
                .build();
    }

    public static List<ContentViewResponseDto> contentViewListResponse(List<Content> contents, Long memberId, MemberContentRepository memberContentRepository) {
        Boolean isCompleted = memberContentRepository.findByContentIdAndMemberId(contents.get(0).getId(), memberId).isPresent();
        return contents.stream()
                .map(content -> {
                    return ContentViewResponseDto.builder()
                            .id(content.getId())
                            .contentName(content.getContentName())
                            .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                            .topic(content.getTopic())
                            .isCompleted(isCompleted)
                            .genre(content.getGenre())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
