package com.gsm.blabla.practice.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Entity
@Getter
@NoArgsConstructor
public class Content {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="content_id")
    private Long id;

    private String contentUrl; // 컨텐츠 URL

    private Duration startedAt; // 컨텐츠 시작 시간

    private Duration stoppedAt; // 컨텐츠 중지 시간

    private Duration endAt; // 컨텐츠 종료 시간

    private String contentName; // 컨텐츠 제목

    private String genre; // 컨텐츠 카테고리

    private String topic; // 컨텐츠 주제

    private String sentence; // 타켓 문장

    private String answer; // 모범 답안

    private String language; // 언어

    @Builder
    public Content(String contentUrl, Duration startedAt, Duration stoppedAt, Duration endAt, String contentName, String genre, String topic, String sentence, String answer, String language) {
        this.contentUrl = contentUrl;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.endAt = endAt;
        this.contentName = contentName;
        this.genre = genre;
        this.topic = topic;
        this.sentence = sentence;
        this.answer = answer;
        this.language = language;
    }
}
