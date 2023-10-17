package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.VoiceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoiceFileRepository extends JpaRepository<VoiceFile, Long> {

    List<VoiceFile> getAllByCrewReportId(Long crewReportId);
    Long countAllByCrewReportId(Long crewReportId);
    List<VoiceFile> findAllByMemberId(Long memberId);
    List<VoiceFile> findAllByCrewReportId(Long reportId);

}
