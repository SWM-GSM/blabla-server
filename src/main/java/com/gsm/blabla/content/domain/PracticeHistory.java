package com.gsm.blabla.content.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PracticeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_content_detail_id")
    private MemberContentDetail memberContentDetail;

    private String practiceUrl; // 연습기록 음성파일 URL
    private LocalDateTime createdAt; // 연습기록 생성일

    @Builder
    public PracticeHistory(MemberContentDetail memberContentDetail, String practiceUrl) {
        this.memberContentDetail = memberContentDetail;
        this.practiceUrl = practiceUrl;
        this.createdAt = LocalDateTime.now();
    }

}
