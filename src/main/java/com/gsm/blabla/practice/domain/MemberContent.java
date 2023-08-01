package com.gsm.blabla.practice.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MemberContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    private String userAnswer; // 유저 답변

    private String shortFeedback; // ENUM 단문 피드백

    @Column(length = 2000)
    private String longFeedback; // GPT 장문 피드백

    private Double starScore; // 별점 - 문장 유사도

    private Double contextScore; // 문맥 점수 - 번역된 문장 유사도

    private LocalDateTime joinedAt;

    @Builder
    public MemberContent(Member member, Content content, String userAnswer, String shortFeedback, String longFeedback, Double starScore, Double contextScore) {
        this.member = member;
        this.content = content;
        this.userAnswer = userAnswer;
        this.shortFeedback = shortFeedback;
        this.longFeedback = longFeedback;
        this.starScore = starScore;
        this.contextScore = contextScore;
        this.joinedAt = LocalDateTime.now();
    }
}
