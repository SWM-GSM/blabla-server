package com.gsm.blabla.content.dto;

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
public class ContentViewResponseDto {
    private Long id;
    private Long level; // 컨텐츠 레벨
    private String topic; // 컨텐츠 주제
    private String title; // 컨텐츠 제목

    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private Boolean isCompleted; // 컨텐츠 완료 여부

    public static ContentViewResponseDto contentViewResponse(Content content, Long memberId, MemberContentRepository memberContentRepository) {

        return ContentViewResponseDto.builder()
                .id(content.getId())
                .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                .level(content.getLevel())
                .topic(content.getTopic())
                .title(content.getTitle())
                .isCompleted(memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isPresent())
                .build();
    }

    public static List<ContentViewResponseDto> contentViewListResponse(List<Content> contents, Long memberId, MemberContentRepository memberContentRepository) {
        return contents.stream()
                .map(content -> {
                    return ContentViewResponseDto.builder()
                            .id(content.getId())
                            .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                            .level(content.getLevel())
                            .topic(content.getTopic())
                            .title(content.getTitle())
                            .isCompleted(memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isPresent())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
