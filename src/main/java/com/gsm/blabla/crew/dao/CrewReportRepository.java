package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrewReportRepository extends JpaRepository<CrewReport, Long> {

    List<CrewReport> findAllByCrew(Crew crew);

    @Query("SELECT max(cr.id) FROM CrewReport cr where cr.crew.id = :crewId")
    Long findCurrentId(Long crewId);
}
