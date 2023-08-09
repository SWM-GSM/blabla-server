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
    private String contentName; // 컨텐츠 제목
    private Double progress; // 연습실
    private List<ContentViewResponseDto> contents; // 컨텐츠 리스트

    public static ContentListResponseDto contentListResponse(List<ContentViewResponseDto> contents) {
        int totalContents = contents.size();
        long completedCount = contents.stream()
                .filter(ContentViewResponseDto::getIsCompleted)
                .count();

        double overallProgress = (completedCount / (double) totalContents) * 100.0;
        double roundedOverallProgress = Math.min(100.0, overallProgress);
        double overallProgressPercentage = Math.round(roundedOverallProgress * 10) / 10.0;

        return ContentListResponseDto.builder()
                .contentName(contents.get(0).getContentName())
                .progress(overallProgressPercentage)
                .contents(contents)
                .build();
    }
}
