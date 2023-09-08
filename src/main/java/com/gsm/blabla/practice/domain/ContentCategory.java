package com.gsm.blabla.practice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ContentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 컨텐츠 제목
    private String subtitle; // 컨텐츠 소제목
    private String description; // 컨텐츠 설명
    private String language; // 언어
    private String thumbnail; // 컨텐츠 썸네일 URL
}
