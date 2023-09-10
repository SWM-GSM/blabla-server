package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentsResponseDto {

    private Long id;
    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private Double progress; // 연습실 진행도

    public static ContentsResponseDto contentCategoryResponse(Content content, List<ContentDetailDto> contentDetailDtoList) {
        long totalContents = contentDetailDtoList.size();
        long completedCount = contentDetailDtoList.stream()
                .filter(ContentDetailDto::getIsCompleted)
                .count();

        double overallProgress = (completedCount / (double) totalContents) * 100.0;
        double roundedOverallProgress = Math.min(100.0, overallProgress);
        double overallProgressPercentage = Math.round(roundedOverallProgress * 10) / 10.0;

        return ContentsResponseDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .thumbnailUrl(content.getThumbnailURL())
                .description(content.getDescription())
                .progress(overallProgressPercentage)
                .build();
    }

}
