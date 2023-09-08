package com.gsm.blabla.practice.dto;

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
    private String subtitle; // 컨텐츠 소제목
    private String description; // 컨텐츠 설명
    private Double progress; // 연습실
    private String thumbnail; // 컨텐츠 썸네일 URL

    public static ContentListResponseDto contentListResponse(List<ContentViewResponseDto> contents) {
        int totalContents = contents.size();
        long completedCount = contents.stream()
                .filter(ContentViewResponseDto::getIsCompleted)
                .count();

        double overallProgress = (completedCount / (double) totalContents) * 100.0;
        double roundedOverallProgress = Math.min(100.0, overallProgress);
        double overallProgressPercentage = Math.round(roundedOverallProgress * 10) / 10.0;

        return ContentListResponseDto.builder()
                .title(contents.get(0).getTitle())
                .progress(overallProgressPercentage)
                .thumbnail(contents.get(0).getYoutubeId())
                .description(contents.get(0).getDescription())
                .build();
    }
}
