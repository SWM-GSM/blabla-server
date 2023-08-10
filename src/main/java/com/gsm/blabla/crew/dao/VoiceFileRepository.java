package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.VoiceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoiceFileRepository extends JpaRepository<VoiceFile, Long> {
    List<VoiceFile> getAllByCrewReportId(Long crewReportId);

    List<VoiceFile> findAllByMemberId(Long memberId);
}
