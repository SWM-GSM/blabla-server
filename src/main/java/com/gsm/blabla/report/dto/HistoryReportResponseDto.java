package com.gsm.blabla.report.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryReportResponseDto {

    private Long id;
    private String type; // crew, personal
    private Map<String, String> info;
    private String dateTime;

    public static HistoryReportResponseDto of(Long id, String type, Map<String, String> info) {
        return HistoryReportResponseDto.builder()
            .id(id)
            .type(type)
            .info(info)
            .build();
    }
}
