package com.gsm.blabla.practice.dto;

import com.gsm.blabla.practice.dao.MemberContentRepository;
import com.gsm.blabla.practice.domain.Content;
import com.gsm.blabla.practice.domain.ContentCategory;
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
    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명

    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private Boolean isCompleted; // 컨텐츠 완료 여부

    public static ContentViewResponseDto contentViewResponse(ContentCategory contentCategory, Content content, Long memberId, MemberContentRepository memberContentRepository) {

        return ContentViewResponseDto.builder()
                .id(content.getId())
                .title(contentCategory.getTitle())
                .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                .description(content.getDescription())
                .isCompleted(memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isPresent())
                .build();
    }

    public static List<ContentViewResponseDto> contentViewListResponse(List<Content> contents, Long memberId, MemberContentRepository memberContentRepository) {
        return contents.stream()
                .map(content -> {
                    return ContentViewResponseDto.builder()
                            .id(content.getId())
                            .title(content.getContentCategory().getTitle())
                            .thumbnailUrl("https://img.youtube.com/vi/" + content.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                            .isCompleted(memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isPresent())
                            .description(content.getDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
