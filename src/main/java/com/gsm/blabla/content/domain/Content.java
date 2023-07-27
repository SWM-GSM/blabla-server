package com.gsm.blabla.content.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Content {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="content_id")
    private Long id;

    private String contentUrl; // 컨텐츠 URL

    private Long level; // 컨텐츠 레벨

    private String sentence; // 타켓 문장

    private String answer; // 모범 답안

    private String language; // 언어

    private String topic; // 컨텐츠 주제

    private String title; // 컨텐츠 제목

    @Builder
    public Content(Long id, String contentUrl, Long level, String sentence, String answer, String language, String topic, String title) {
        this.id = id;
        this.contentUrl = contentUrl;
        this.level = level;
        this.sentence = sentence;
        this.answer = answer;
        this.language = language;
        this.topic = topic;
        this.title = title;
    }
}
