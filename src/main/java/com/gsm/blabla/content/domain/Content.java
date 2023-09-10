package com.gsm.blabla.content.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String language; // 언어
    private String thumbnailURL; // 컨텐츠 썸네일 URL

    @Builder
    public Content(String title, String description, String language, String thumbnailURL) {
        this.title = title;
        this.description = description;
        this.language = language;
        this.thumbnailURL = thumbnailURL;
    }

}
