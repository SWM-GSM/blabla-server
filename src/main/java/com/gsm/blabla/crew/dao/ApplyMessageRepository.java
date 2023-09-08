package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.ApplyMessage;
import com.gsm.blabla.crew.domain.ApplyMessageStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyMessageRepository extends JpaRepository<ApplyMessage, Long> {
    Optional<ApplyMessage> getByCrewIdAndMemberId(Long crewId, Long memberId);
}
