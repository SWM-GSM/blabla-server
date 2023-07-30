package com.gsm.blabla.practice.api;

import com.gsm.blabla.practice.application.PracticeService;
import com.gsm.blabla.practice.dto.ContentListResponseDto;
import com.gsm.blabla.practice.dto.ContentResponseDto;
import com.gsm.blabla.practice.dto.ContentViewResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.practice.dto.PracticeFeedbackResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "연습실 관련 API")
@RestController
@RequiredArgsConstructor
public class PracticeController {
    private final PracticeService practiceService;

    @Operation(summary = "단일 컨텐츠 조회 API")
    @GetMapping("/contents/{contentId}")
    public DataResponseDto<ContentResponseDto> get(@PathVariable Long contentId) {
        return DataResponseDto.of(practiceService.get(contentId));
    }

    @Operation(summary = "컨텐츠 전체 조회 API")
    @GetMapping("/{language}/contents")
    public DataResponseDto<Map<String, ContentListResponseDto>> getAll(@PathVariable String language) {
        return DataResponseDto.of(practiceService.getAll(language));
    }

    @Operation(summary = "오늘의 컨텐츠 조회 API")
    @GetMapping("/{language}/contents/today")
    public DataResponseDto<ContentViewResponseDto> getTodayContent(@PathVariable String language) {
        return DataResponseDto.of(practiceService.getTodayContent(language));
    }

    @Operation(summary = "연습실 피드백 생성 & 조회 API")
    @PostMapping("/contents/{contentId}/feedback")
    public DataResponseDto<PracticeFeedbackResponseDto> createFeedback(
            @PathVariable Long contentId,
            @RequestParam("userAnswer") String userAnswer) {
        return DataResponseDto.of(practiceService.createFeedback(contentId, userAnswer));
    }

    @Operation(summary = "연습실 피드백 조회 API")
    @GetMapping("/contents/{contentId}/feedback")
    public DataResponseDto<PracticeFeedbackResponseDto> getFeedback(
            @PathVariable Long contentId) {
        return DataResponseDto.of(practiceService.getFeedback(contentId));
    }
}


