package com.gsm.blabla.report.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class HistoryResponseDto {

    private String datetime;
    private List<HistoryReportResponseDto> reports;
}
