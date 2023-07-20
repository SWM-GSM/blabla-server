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
        Map<Integer, Long> completedContentsByLevel = contents.stream()
                .filter(ContentViewResponseDto::isCompleted)
                .collect(Collectors.groupingBy(ContentViewResponseDto::getLevel, Collectors.counting()));

        long completedOverall = completedContentsByLevel.getOrDefault(10, 0L);
        double overallProgress = (completedOverall / (double) totalContents) * 100.0;

        int overallProgressPercentage = (int) Math.min(100, overallProgress);

        return ContentListResponseDto.builder()
                .progress(overallProgressPercentage)
                .contents(contents)
                .build();
    }
}
