package com.gsm.blabla.crew.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CrewReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="crew_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private LocalDateTime startedAt;
    private LocalDateTime endAt;

    private Duration koreanTime;
    private Duration englishTime;
    private String cloudUrl;
    private LocalDateTime createdAt;

    @Builder
    public CrewReport(Crew crew, LocalDateTime startedAt, LocalDateTime endAt, Duration koreanTime, Duration englishTime, String cloudUrl) {
        this.crew = crew;
        this.startedAt = startedAt;
        this.endAt = endAt;
        this.koreanTime = koreanTime;
        this.englishTime = englishTime;
        this.cloudUrl = cloudUrl;
        this.createdAt = LocalDateTime.now();
    }
}
