package com.gsm.blabla.crew.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.crew.dto.CrewReportResponseDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.member.dto.MemberRequestDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CrewControllerTest extends ControllerTestSupport {

    MemberRequestDto memberRequestDto;

    @BeforeEach
    void setUp() {
        memberRequestDto = MemberRequestDto.builder()
            .socialLoginType("TEST")
            .nickname("테스트")
            .profileImage("cat")
            .birthDate("2001-01-01")
            .gender("male")
            .countryCode("KR")
            .korLevel(5)
            .engLevel(5)
            .pushNotification(false)
            .build();
    }

    @DisplayName("[POST] 크루를 생성한다.")
    @Test
    @WithCustomMockUser
    void create() throws Exception {
        // given
        CrewRequestDto crewRequestDto = CrewRequestDto.builder()
            .coverImage("test")
            .name("테스트")
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(8)
            .korLevel(1)
            .engLevel(1)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(true)
            .build();

        given(crewService.create(any(CrewRequestDto.class)))
            .willReturn(Collections.singletonMap("crewId", 1L));

        // when // then
        mockMvc.perform(
            post("/crews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crewRequestDto))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.crewId").exists())
            ;
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
            MemberResponseDto.builder().nickname("테스트1").profileImage("cat").comment("테스트1").build(),
            MemberResponseDto.builder().nickname("테스트2").profileImage("dog").comment("테스트2").build(),
            MemberResponseDto.builder().nickname("테스트3").profileImage("lion").comment("테스트3").build(),
            MemberResponseDto.builder().nickname("테스트4").profileImage("bear").comment("테스트4").build()
        );

        given(crewService.getReport(any(Long.class)))
            .willReturn(CrewReportResponseDto.crewReportResponse(info, members, bubbleChart, keyword,
                languageRatio, feedbacks));

        // when // then
        mockMvc.perform(
            get("/crews/reports/1")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.info.createdAt").value("2023.05.30 16:00"))
            .andExpect(jsonPath("$.data.info.durationTime").value("00:23:40"))
            .andExpect(jsonPath("$.data.members").isArray())
            .andExpect(jsonPath("$.data.bubbleChart").value("www.test.com"))
            .andExpect(jsonPath("$.data.keyword").isArray())
            .andExpect(jsonPath("$.data.languageRatio").isMap())
            .andExpect(jsonPath("$.data.feedbacks").isArray());
    }

    @DisplayName("[GET] 크루 리포트 목록을 조회한다.")
    @Test
    @WithCustomMockUser
    void getAllReports() throws Exception {
        // given
        Map<String, List<CrewReportResponseDto>> reports = new HashMap<>();
        List<MemberResponseDto> members = getMembers();
        Map<String, String> info = getInfo();
        reports.put("reports", Collections.nCopies(6, CrewReportResponseDto.crewReportListResponse(
            1L, true, members, info)));

        given(crewService.getAllReports(anyLong(), anyString()))
            .willReturn(reports);

        // when // then
        mockMvc.perform(
            get("/crews/1/reports")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reports").isArray());
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
