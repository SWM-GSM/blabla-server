package com.gsm.blabla.practice.dao;

import com.gsm.blabla.practice.domain.PracticeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeHistoryRepository extends JpaRepository<PracticeHistory, Long> {
}
