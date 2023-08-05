package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
