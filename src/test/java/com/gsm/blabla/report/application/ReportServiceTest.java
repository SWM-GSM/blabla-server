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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
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

    @DisplayName("[GET] 히스토리 조회 시나리오")
    @TestFactory
    @WithCustomMockUser
    Collection<DynamicTest> getHistory() {
            // given
            return List.of(
                DynamicTest.dynamicTest("크루 리포트만 있을 경우", () -> {
                    //given
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    createReport(member1, member2, now.plusDays(1));
                    createReport(member1, member2, now);
                    createReport(member1, member2, now.plusDays(2));

                    // when
                    List<HistoryResponseDto> histories = reportService.getHistory().get("histories");

                    // then
                    assertThat(histories).hasSize(3);
                    assertThat(histories.get(0)).extracting("datetime").isEqualTo(now.plusDays(2).format(formatter));
                    assertThat(histories.get(1)).extracting("datetime").isEqualTo(now.plusDays(1).format(formatter));
                    assertThat(histories.get(2)).extracting("datetime").isEqualTo(now.format(formatter));

                    assertThat(histories.get(0).getReports().get(0).getInfo().get("title")).isEqualTo("테스트1 외 1명");
                }),
                DynamicTest.dynamicTest("연습실 컨텐츠만 있을 경우", () -> {
                    //given

                    // when

                    // then
                }),
                DynamicTest.dynamicTest("크루 리포트와 연습실 컨텐츠 둘 다 있을 경우", () -> {
                    //given

                    // when

                    // then
                })
            );
    }
}
