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
public class ContentDetailsResponseDto {

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private List<ContentDetailDto> contentDetails; // 컨텐츠 리스트

    public static ContentDetailsResponseDto contentListResponse(Content content, List<ContentDetailDto> contentDetailDtoList) {
        return ContentDetailsResponseDto.builder()
                .title(content.getTitle())
                .description(content.getDescription())
                .thumbnailUrl(content.getThumbnailURL())
                .contentDetails(contentDetailDtoList)
                .build();
    }

}
