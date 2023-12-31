package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.CrewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrewReportRepository extends JpaRepository<CrewReport, Long> {

    @Query("SELECT max(cr.id) FROM CrewReport cr")
    Long findCurrentId();
}
