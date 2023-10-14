package com.gsm.blabla.report.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("히스토리를 조회한다.")
    class getHistory {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Test
        @DisplayName("크루 리포트만 있을 경우")
        @WithCustomMockUser
        void getHistoryWithOnlyCrewReport() {
            //given
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

            assertThat(histories.get(0).getReports().get(0).getType()).isEqualTo("crew");
            assertThat(histories.get(0).getReports().get(0).getInfo().get("title")).isEqualTo("테스트1 외 1명");
            assertThat(histories.get(0).getReports().get(0).getInfo().get("subTitle")).isEqualTo("00:26:30");
        }

        @Test
        @DisplayName("컨텐츠 연습 기록만 있을 경우")
        @WithCustomMockUser
        void getHistoryWithOnlyMemberContentDetail() {
            // given
            createMemberContentDetail(member1);
            createMemberContentDetail(member1);

            // when
            List<HistoryResponseDto> histories = reportService.getHistory().get("histories");

            // then
            assertThat(histories).hasSize(1);
            assertThat(histories.get(0)).extracting("datetime").isEqualTo(now.format(formatter));

            assertThat(histories.get(0).getReports().get(0).getType()).isEqualTo("personal");
            assertThat(histories.get(0).getReports().get(0).getInfo().get("title")).isEqualTo("다짐하는 표현");
            assertThat(histories.get(0).getReports().get(0).getInfo().get("subTitle")).isEqualTo("주토피아");
        }

        @Test
        @DisplayName("둘 다 있을 경우")
        @WithCustomMockUser
        void getHistoryWithBothData() {
            // given
            createReport(member1, member2, now.plusDays(1));
            createReport(member1, member2, now);
            createReport(member1, member2, now.plusDays(2));

            createMemberContentDetail(member1);
            createMemberContentDetail(member1);

            // when
            List<HistoryResponseDto> histories = reportService.getHistory().get("histories");

            // then
            assertThat(histories).hasSize(3);

            assertThat(histories.get(0)).extracting("datetime").isEqualTo(now.plusDays(2).format(formatter));
            assertThat(histories.get(1)).extracting("datetime").isEqualTo(now.plusDays(1).format(formatter));
            assertThat(histories.get(2)).extracting("datetime").isEqualTo(now.format(formatter));

            assertThat(histories.get(2).getReports()).hasSize(3);
            assertThat(histories.get(2).getReports().get(0).getType()).isEqualTo("crew");
            assertThat(histories.get(2).getReports().get(1).getType()).isEqualTo("personal");
            assertThat(histories.get(2).getReports().get(2).getType()).isEqualTo("personal");
        }
    }
}
