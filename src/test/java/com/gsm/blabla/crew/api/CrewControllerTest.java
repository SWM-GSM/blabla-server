package com.gsm.blabla.crew.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.crew.dto.CrewReportResponseDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.dto.MemberRequestDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrewControllerTest extends ControllerTestSupport {

    MemberRequestDto memberRequestDto;

    @BeforeEach
    void setUp() {
        memberRequestDto = MemberRequestDto.builder()
            .socialLoginType("TEST")
            .learningLanguage("ko")
            .build();
    }

    @DisplayName("[GET] 크루 리포트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getReport() throws Exception {
        // given
        Map<String, String> info = getInfo();
        List<MemberResponseDto> members = getMembers();
        String bubbleChart = "www.test.com";
        List<Map<String, Object>> keyword = List.of(
            Map.of("name", "미국 유머", "count", 38),
            Map.of("name", "중국 유머", "count", 28),
            Map.of("name", "한국 유머", "count", 18),
            Map.of("name", "영국 유머", "count", 8)
        );
        Map<String, Integer> languageRatio = Map.of(
            "korean", 60,
            "eng", 40
        );
        List<MemberResponseDto> feedbacks = List.of(
            MemberResponseDto.builder().nickname("테스트1").profileImage("wolf").comment("테스트1").build(),
            MemberResponseDto.builder().nickname("테스트2").profileImage("dog").comment("테스트2").build(),
            MemberResponseDto.builder().nickname("테스트3").profileImage("lion").comment("테스트3").build()
        );

        given(crewService.getReport(any(Long.class)))
            .willReturn(CrewReportResponseDto.crewReportResponse(info, members, bubbleChart, keyword,
                languageRatio, feedbacks));

        // when // then
        mockMvc.perform(
            get("/api/v1/crews/reports/{reportId}", any(Long.class))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.info.createdAt").value("2023.05.30 16:00"))
            .andExpect(jsonPath("$.data.info.durationTime").value("00:23:40"))
            .andExpect(jsonPath("$.data.members", hasSize(4)))
            .andExpect(jsonPath("$.data.bubbleChart").value("www.test.com"))
            .andExpect(jsonPath("$.data.keyword", hasSize(4)))
            .andExpect(jsonPath("$.data.languageRatio").isMap())
            .andExpect(jsonPath("$.data.feedbacks", hasSize(3)));
    }

    private Map<String, String> getInfo() {
        return Map.of(
            "createdAt", "2023.05.30 16:00",
            "durationTime", "00:23:40"
        );
    }
    private List<MemberResponseDto> getMembers() {
        return List.of(
            MemberResponseDto.builder().id(1L).nickname("테스트1").profileImage("cat").build(),
            MemberResponseDto.builder().id(2L).nickname("테스트2").profileImage("dog").build(),
            MemberResponseDto.builder().id(3L).nickname("테스트3").profileImage("lion").build(),
            MemberResponseDto.builder().id(4L).nickname("테스트4").profileImage("bear").build()
        );
    }
}
