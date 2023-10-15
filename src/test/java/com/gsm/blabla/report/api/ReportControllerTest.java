package com.gsm.blabla.report.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.report.dto.HistoryReportResponseDto;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReportControllerTest extends ControllerTestSupport {

    @DisplayName("히스토리를 조회한다.")
    @Test
    @WithCustomMockUser
    void getHistory() throws Exception {
        // given
        List<HistoryResponseDto> histories = List.of(
            HistoryResponseDto.builder()
                .datetime("2023-01-01")
                .reports(List.of(
                    createHistoryReportResponseDto(1L, "personal", "시간 약속 잡기", "애니메이션 - 아이스 베어"),
                    createHistoryReportResponseDto(2L, "crew", "민감자네 크루", "00:23:40"),
                    createHistoryReportResponseDto(3L, "crew", "민감자네 크루", "00:23:40"),
                    createHistoryReportResponseDto(4L, "personal", "인사 표현 배우기", "드라마 - 스물다섯 스물하나")
                ))
                .build(),
            HistoryResponseDto.builder()
                .datetime("2023-02-01")
                .reports(List.of(
                    createHistoryReportResponseDto(1L, "personal", "시간 약속 잡기", "애니메이션 - 아이스 베어"),
                    createHistoryReportResponseDto(2L, "crew", "민감자네 크루", "00:23:40"),
                    createHistoryReportResponseDto(3L, "crew", "민감자네 크루", "00:23:40"),
                    createHistoryReportResponseDto(4L, "personal", "인사 표현 배우기", "드라마 - 스물다섯 스물하나")
                ))
                .build()

        );
        Map<String, List<HistoryResponseDto>> reports = Map.of("histories", histories);
        given(reportService.getHistory())
            .willReturn(reports);

        // when // then
        mockMvc.perform(
            get("/api/v1/reports/history")
                .with(csrf())
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.histories").isArray())
        ;
    }

    HistoryReportResponseDto createHistoryReportResponseDto(Long id, String type, String title, String subTitle) {
        return HistoryReportResponseDto.of(id, type, Map.of("title", title, "subTitle", subTitle));
    }
}
