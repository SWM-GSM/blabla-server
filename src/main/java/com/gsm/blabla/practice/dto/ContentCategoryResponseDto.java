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
public class ContentCategoryResponseDto {
    private Long id;
    private String title; // 컨텐츠 제목
    private String subtitle; // 컨텐츠 소제목
    private String description; // 컨텐츠 설명
    private String thumbnail; // 컨텐츠 썸네일 URL
    private Double progress; // 연습실 진행도

    public static ContentCategoryResponseDto contentCategoryResponse(ContentCategory contentCategory, List<ContentViewResponseDto> contents) {
        int totalContents = contents.size();
        long completedCount = contents.stream()
                .filter(ContentViewResponseDto::getIsCompleted)
                .count();

        double overallProgress = (completedCount / (double) totalContents) * 100.0;
        double roundedOverallProgress = Math.min(100.0, overallProgress);
        double overallProgressPercentage = Math.round(roundedOverallProgress * 10) / 10.0;

        return ContentCategoryResponseDto.builder()
                .id(contentCategory.getId())
                .title(contentCategory.getTitle())
                .thumbnail(contentCategory.getThumbnail())
                .subtitle(contentCategory.getSubtitle())
                .description(contentCategory.getDescription())
                .progress(overallProgressPercentage)
                .build();
    }

}
