package com.gsm.blabla.crew.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CrewReportKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_report_id")
    private CrewReport crewReport;

    private String keyword;
    private Long count;

    @Builder
    public CrewReportKeyword(CrewReport crewReport, String keyword, Long count) {
        this.crewReport = crewReport;
        this.keyword = keyword;
        this.count = count;
    }

}
