package com.gsm.blabla.report.api;

import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.report.application.ReportService;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report 관련 API")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "히스토리 조회 API")
    @GetMapping(value = "/history")
    public DataResponseDto<Map<String, List<HistoryResponseDto>>> getHistory() {
        return DataResponseDto.of(reportService.getHistory());
    }
}
