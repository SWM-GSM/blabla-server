package com.gsm.blabla.report.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReportServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;
    LocalDateTime now = LocalDateTime.now();

    @Autowired
    ReportService reportService;

    @BeforeEach
    void setUp() {
        member1 = createMember("테스트1", "cat");
        member2 = createMember("테스트2", "dog");
    }

    @DisplayName("[GET] 히스토리를 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getHistory() {
        // TODO: 수정 필요
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);

        CrewReport crewReport1 = createReport(member1, member2, now);
        CrewReport crewReport2 = createReport(member1, member2, now.plusDays(1));
        CrewReport crewReport3 = createReport(member1, member2, now.plusDays(2));

        // when
        List<HistoryResponseDto> histories = reportService.getHistory().get("histories");

        // then
        assertThat(histories).hasSize(3);
    }
}
