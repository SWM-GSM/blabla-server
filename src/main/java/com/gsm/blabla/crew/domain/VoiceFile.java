package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class VoiceFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="voice_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_report_id")
    private CrewReport crewReport;

    private String fileUrl;

    private Duration totalCallTime;
    private Duration koreanTime;
    private Duration englishTime;
    private Duration redundancyTime;
    private LocalDateTime createdAt;

    @Builder
    public VoiceFile(Member member, CrewReport crewReport, String fileUrl, Duration totalCallTime, Duration koreanTime, Duration englishTime, Duration redundancyTime, LocalDateTime createdAt) {
        this.member = member;
        this.crewReport = crewReport;
        this.fileUrl = fileUrl;
        this.totalCallTime = totalCallTime;
        this.koreanTime = koreanTime;
        this.englishTime = englishTime;
        this.redundancyTime = redundancyTime;
        this.createdAt = LocalDateTime.now();
    }
}
