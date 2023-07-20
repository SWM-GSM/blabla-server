package com.gsm.blabla.content.api;

import com.gsm.blabla.content.application.ContentService;
import com.gsm.blabla.content.dto.ContentListResponseDto;
import com.gsm.blabla.content.dto.ContentResponseDto;
import com.gsm.blabla.content.dto.ContentViewResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "연습실 관련 API")
@RestController
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @Operation(summary = "단일 컨텐츠 조회 API")
    @GetMapping("/contents/{contentId}")
    public DataResponseDto<Map<String, ContentResponseDto>> get(@PathVariable Long contentId) {
        return DataResponseDto.of(contentService.get(contentId));
    }

    @Operation(summary = "컨텐츠 전체 조회 API")
    @GetMapping("/{language}/contents")
    public DataResponseDto<Map<String, ContentListResponseDto>> getAll(@PathVariable String language) {
        return DataResponseDto.of(contentService.getAll(language));
    }

    @Operation(summary = "오늘의 컨텐츠 조회 API")
    @GetMapping("/{language}/contents/today")
    public DataResponseDto<Map<String, ContentViewResponseDto>> getTodayContent(@PathVariable String language) {
        return DataResponseDto.of(contentService.getTodayContent(language));
    }
}


