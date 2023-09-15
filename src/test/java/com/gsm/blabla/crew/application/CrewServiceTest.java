package com.gsm.blabla.crew.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.dto.CrewReportResponseDto;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDateTime;
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
        member1 = createMember("테스트", "dog");
        member2 = createMember("테스트", "wolf");
    }

    @DisplayName("[GET] 크루 리포트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getReport() {
        // given
        CrewReport crewReport = createReport(member1, member2, now);

        String durationTimeToString = String.format("%02d:%02d:%02d", 0, 26, 30);

        // when
        CrewReportResponseDto response = crewService.getReport(crewReport.getId());

        // then
        assertThat(response.getInfo()).extracting("createdAt")
            .matches(createdAt -> Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}", createdAt.toString()));

        assertThat(response.getInfo()).containsEntry("durationTime", durationTimeToString);

        assertThat(response.getMembers()).hasSize(2)
            .extracting("profileImage")
            .containsExactlyInAnyOrder(member1.getProfileImage(), member2.getProfileImage());

        assertThat(response.getBubbleChart()).isEqualTo("www.test.com");

        assertThat(response.getKeyword()).extracting("name")
            .containsExactlyInAnyOrder("테스트1", "테스트2", "테스트3");

        assertThat(response.getLanguageRatio()).containsEntry("korean", 76);

        assertThat(response.getLanguageRatio()).containsEntry("english", 24);

        assertThat(response.getFeedbacks()).hasSize(2)
            .extracting("comment")
            .containsExactlyInAnyOrder(
                String.format("테스트 피드백 by %s", member1.getProfileImage()),
                String.format("테스트 피드백 by %s", member2.getProfileImage())
            );
    }
}
