package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewReportRepository extends JpaRepository<CrewReport, Long> {

    Optional<List<CrewReport>> findAllByCrew(Crew crew);
}
