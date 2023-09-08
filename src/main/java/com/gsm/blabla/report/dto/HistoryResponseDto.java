package com.gsm.blabla.report.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryResponseDto {

    private String datetime;
    private List<HistoryReportResponseDto> reports;
}
