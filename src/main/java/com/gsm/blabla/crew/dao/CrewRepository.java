package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long> {
}
