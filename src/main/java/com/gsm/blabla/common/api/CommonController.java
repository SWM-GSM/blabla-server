package com.gsm.blabla.common.api;

import com.gsm.blabla.common.application.CommonService;
import com.gsm.blabla.common.dto.CommonCodeDto;
import com.gsm.blabla.common.dto.KeywordDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 코드 관련 API")
@RestController
@RequestMapping("/{language}/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @Operation(summary = "레벨 별 문구 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / en")
    @GetMapping("/levels")
    public DataResponseDto<Map<String, String>> getLevels(@PathVariable String language) {
        return DataResponseDto.of(commonService.getLevels(language));
    }

    @Operation(summary = "키워드 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / en")
    @GetMapping("/keywords")
    public DataResponseDto<Map<String, List<KeywordDto>>> getKeywords(@PathVariable String language) {
        return DataResponseDto.of(commonService.getKeywords(language));
    }

    @Operation(summary = "크루 태그 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / en")
    @GetMapping("/crew-tags")
    public DataResponseDto<Map<String, List<CommonCodeDto>>> getCrewTags(@PathVariable String language) {
        return DataResponseDto.of(commonService.getCrewTags(language));
    }

    @Operation(summary = "희망 크루원 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / en")
    @GetMapping("/prefer-members")
    public DataResponseDto<Map<String, List<CommonCodeDto>>> getPreferMembers(@PathVariable String language) {
        return DataResponseDto.of(commonService.getPreferMembers(language));
    }
}
