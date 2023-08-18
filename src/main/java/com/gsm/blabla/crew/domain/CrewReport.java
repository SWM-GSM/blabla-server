package com.gsm.blabla.crew.domain;

import jakarta.persistence.*;
import java.util.List;
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
    private LocalDateTime endAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0);

    @OneToMany(mappedBy = "crewReport")
    private List<VoiceFile> voiceFiles;

    @OneToMany(mappedBy = "crewReport")
    private List<CrewReportKeyword> keywords;

    @Builder
    public CrewReport(Crew crew, LocalDateTime startedAt) {
        this.crew = crew;
        this.startedAt = startedAt;
    }

    public void updateEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }
}
