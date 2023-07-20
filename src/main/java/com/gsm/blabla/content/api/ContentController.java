package com.gsm.blabla.content.api;

import com.gsm.blabla.content.application.ContentService;
import com.gsm.blabla.content.dto.ContentResponseDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}


