package com.gsm.blabla.practice.dto;

import com.gsm.blabla.practice.domain.ContentCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentListResponseDto {

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private List<ContentViewResponseDto> contents; // 컨텐츠 리스트

    public static ContentListResponseDto contentListResponse(ContentCategory contentCategory, List<ContentViewResponseDto> contentViewResponseDtoList) {
        return ContentListResponseDto.builder()
                .title(contentCategory.getTitle())
                .description(contentCategory.getDescription())
                .thumbnailUrl(contentCategory.getThumbnailURL())
                .contents(contentViewResponseDtoList)
                .build();
    }
}
