package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.*;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CrewReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startedAt;
    private LocalDateTime endAt = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

    @OneToMany(mappedBy = "crewReport")
    private List<VoiceFile> voiceFiles;

    @OneToMany(mappedBy = "crewReport")
    private List<CrewReportKeyword> keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public CrewReport(LocalDateTime startedAt, LocalDateTime endAt, Member member) {
        this.startedAt = startedAt;
        this.endAt = endAt;
        this.member = member;
    }

    public void updateEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }
}
