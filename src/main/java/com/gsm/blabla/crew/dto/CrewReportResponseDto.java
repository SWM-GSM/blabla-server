package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewReportResponseDto {
    private Long id;
    private Boolean generated;
    private Map<String, String> info;
    private List<MemberResponseDto> members;
    private String bubbleChart;
    private List<Map<String, Object>> keyword;
    private Map<String, Integer> languageRatio;
    private List<MemberResponseDto> feedbacks;

    public static CrewReportResponseDto crewReportResponse(Map<String, String> info, List<MemberResponseDto> members,
        String bubbleChart, List<Map<String, Object>> keyword, Map<String, Integer> languageRatio,
        List<MemberResponseDto> feedbacks) {
        return CrewReportResponseDto.builder()
            .info(info)
            .members(members)
            .bubbleChart(bubbleChart)
            .keyword(keyword)
            .languageRatio(languageRatio)
            .feedbacks(feedbacks)
            .build();
    }

    public static CrewReportResponseDto crewReportListResponse(Long id, Boolean generated, List<MemberResponseDto> members,
        Map<String, String> info) {
        return CrewReportResponseDto.builder()
            .id(id)
            .generated(generated)
            .members(members)
            .info(info)
            .build();
    }
}
