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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_category_id")
    private ContentCategory contentCategory;

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명

    private String guideSentence; // 타켓 문장
    private String targetSentence; // 모범 답안

    private String contentUrl; // 컨텐츠 URL

    @Column(columnDefinition = "TIME")
    private Duration startedAt; // 컨텐츠 시작 시간
    @Column(columnDefinition = "TIME")
    private Duration stoppedAt; // 컨텐츠 중지 시간
    @Column(columnDefinition = "TIME")
    private Duration endedAt; // 컨텐츠 종료 시간

    @Builder
    public Content(ContentCategory contentCategory, String contentUrl, String title, String description, String guideSentence, String targetSentence, Duration startedAt, Duration stoppedAt, Duration endedAt) {
        this.contentCategory = contentCategory;
        this.contentUrl = contentUrl;
        this.title = title;
        this.description = description;
        this.guideSentence = guideSentence;
        this.targetSentence = targetSentence;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.endedAt = endedAt;
    }

}
