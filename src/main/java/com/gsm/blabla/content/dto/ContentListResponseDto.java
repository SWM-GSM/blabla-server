package com.gsm.blabla.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentListResponseDto {
    private double progress; // 연습실
    private List<ContentViewResponseDto> contents; // 컨텐츠 리스트

    public static ContentListResponseDto contentListResponse(List<ContentViewResponseDto> contents) {
        int totalContents = contents.size();
        long completedCount = contents.stream()
                .filter(ContentViewResponseDto::isCompleted)
                .count();

        double overallProgress = (completedCount / (double) totalContents) * 100.0;
        double roundedOverallProgress = Math.min(100.0, overallProgress);
        double overallProgressPercentage = Math.round(roundedOverallProgress * 10) / 10.0;

        return ContentListResponseDto.builder()
                .progress(overallProgressPercentage)
                .contents(contents)
                .build();
    }
}
