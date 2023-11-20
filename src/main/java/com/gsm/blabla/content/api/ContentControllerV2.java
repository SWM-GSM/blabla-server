package com.gsm.blabla.content.api;

import com.gsm.blabla.content.application.ContentService;
import com.gsm.blabla.content.dto.ContentsResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "컨텐츠 관련 API")
@RestController
@RequestMapping("/api/v2/contents")
@RequiredArgsConstructor
public class ContentControllerV2 {

    private final ContentService contentService;

    @Operation(summary = "컨텐츠 전체 조회 API")
    @GetMapping("")
    public DataResponseDto<Map<String, List<ContentsResponseDto>>> getContents(
        @Pattern(regexp = "^(ko|en|cn|jp)$", message = "언어는 ko 또는 en 중 하나여야 합니다.")
        @RequestHeader(name="Content-Language") String language) {
        return DataResponseDto.of(contentService.getContents(language));
    }
}
