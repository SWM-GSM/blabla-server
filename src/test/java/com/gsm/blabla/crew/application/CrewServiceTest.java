package com.gsm.blabla.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.dto.CrewReportResponseDto;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CrewServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;
    LocalDateTime now = LocalDateTime.now();

    @Autowired
    private CrewService crewService;

    @BeforeEach
    void setUp() {
        member1 = createMember("cat");
        member2 = createMember("dog");
    }

    @DisplayName("[GET] 크루 리포트를 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getReport() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);
        CrewReport crewReport = createReport(member1, member2, crew, now);

        String nowToString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String durationTimeToString = String.format("%02d:%02d:%02d", 0, 26, 30);

        // when
        CrewReportResponseDto response = crewService.getReport(crewReport.getId());

        // then
        assertThat(response.getInfo()).extracting("createdAt")
            .matches(createdAt -> Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}", createdAt.toString()));
        assertThat(response.getInfo()).containsEntry("durationTime", durationTimeToString);
        assertThat(response.getMembers()).hasSize(2)
            .extracting("profileImage")
            .containsExactlyInAnyOrder("cat", "dog");
        assertThat(response.getBubbleChart()).isEqualTo("www.test.com");
        assertThat(response.getKeyword()).extracting("name")
            .containsExactlyInAnyOrder("테스트1", "테스트2", "테스트3");
        assertThat(response.getLanguageRatio()).containsEntry("korean", 76);
        assertThat(response.getLanguageRatio()).containsEntry("english", 24);
        assertThat(response.getFeedbacks()).hasSize(2)
            .extracting("comment")
            .containsExactlyInAnyOrder("테스트 피드백 by cat", "테스트 피드백 by dog");
    }

    // TODO: generated = false인 경우에 대해 테스트 케이스 추가
    @DisplayName("[GET] 크루 리포트 목록을 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getAllReports() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);

        CrewReport crewReport1 = createReport(member1, member2, crew, now); // 리포트 1
        CrewReport crewReport2 = createReport(member1, member2, crew, now.plusDays(1)); // 리포트 2
        CrewReport crewReport3 = createReport(member1, member2, crew, now.plusDays(2)); // 리포트 3

        // when
        List<CrewReportResponseDto> response = crewService.getAllReports(crewId, "desc").get("reports");

        // then
        assertThat(response).hasSize(3)
            .extracting("id", "generated")
            .containsExactlyInAnyOrder(
                tuple(crewReport1.getId(), true),
                tuple(crewReport2.getId(), true),
                tuple(crewReport3.getId(), true)
            );
        // TODO: createdAt 검증 좀 더 구체적으로
        assertThat(response).hasSize(3)
            .extracting("info")
            .extracting("createdAt")
            .allMatch(createdAt -> Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}", createdAt.toString()));
        assertThat(response).hasSize(3)
            .extracting("info")
            .extracting("durationTime")
            .containsExactlyInAnyOrder("00:26:30", "00:26:30", "00:26:30");
        // TODO: members에 대한 검증 추가
    }
}
