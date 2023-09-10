package com.gsm.blabla.content.dto;

import com.gsm.blabla.content.dao.MemberContentDetailRepository;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDetailDto {

    private Long id;
    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String thumbnailUrl; // 컨텐츠 썸네일 URL
    private Boolean isCompleted; // 컨텐츠 완료 여부

    public static ContentDetailDto contentViewResponse(Content content, ContentDetail contentDetail, Long memberId, MemberContentDetailRepository memberContentRepository) {
        return ContentDetailDto.builder()
                .id(contentDetail.getId())
                .title(content.getTitle())
                .thumbnailUrl("https://img.youtube.com/vi/" + contentDetail.getContentUrl().split("/watch\\?v=")[1] + "/hqdefault.jpg")
                .description(contentDetail.getDescription())
                .isCompleted(memberContentRepository.findByContentDetailIdAndMemberId(contentDetail.getId(), memberId).isPresent())
                .build();
    }

}
