package com.gsm.blabla.content.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class ContentDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    private String title; // 컨텐츠 제목
    private String description; // 컨텐츠 설명
    private String guideSentence; // 타켓 문장
    private String targetSentence; // 모범 답안
    private String contentUrl; // 컨텐츠 URL
    private LocalTime startedAt; // 컨텐츠 시작 시간
    private LocalTime stoppedAt; // 컨텐츠 중지 시간
    private LocalTime endedAt; // 컨텐츠 종료 시간
    private Long sequence; // 컨텐츠 순서

    @Builder
    public ContentDetail(Content content, String contentUrl, String title, String description, String guideSentence,
        String targetSentence, LocalTime startedAt, LocalTime stoppedAt, LocalTime endedAt, Long sequence) {
        this.content = content;
        this.contentUrl = contentUrl;
        this.title = title;
        this.description = description;
        this.guideSentence = guideSentence;
        this.targetSentence = targetSentence;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.endedAt = endedAt;
        this.sequence = sequence;
    }

}
