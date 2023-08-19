package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewReportAnalysisRepository extends JpaRepository<CrewReportAnalysis, Long> {
    Optional<CrewReportAnalysis> findByCrewReport(CrewReport crewReport);
}
