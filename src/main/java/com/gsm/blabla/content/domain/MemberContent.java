package com.gsm.blabla.content.domain;

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

    private String longFeedback; // GPT 장문 피드백

    private int starRating; // 별점 - 문장 유사도

    private int contextRating; // 문맥 점수 - 번역된 문장 유사도

    private LocalDateTime joinedAt;

    @Builder
    public MemberContent(Long id, Member member, Content content, String userAnswer, String shortFeedback, String longFeedback, int starRating, int contextRating) {
        this.id = id;
        this.member = member;
        this.content = content;
        this.userAnswer = userAnswer;
        this.shortFeedback = shortFeedback;
        this.longFeedback = longFeedback;
        this.starRating = starRating;
        this.contextRating = contextRating;
    }
}
