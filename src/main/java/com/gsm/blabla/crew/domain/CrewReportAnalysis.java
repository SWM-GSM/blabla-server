package com.gsm.blabla.crew.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CrewReportAnalysis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_report_id")
    private CrewReport crewReport;

    private Duration koreanTime;
    private Duration englishTime;
    private String cloudUrl;

    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    @Builder
    public CrewReportAnalysis(CrewReport crewReport, Duration koreanTime, Duration englishTime, String cloudUrl, LocalDateTime endAt) {
        this.crewReport = crewReport;
        this.koreanTime = koreanTime;
        this.englishTime = englishTime;
        this.cloudUrl = cloudUrl;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }
}
