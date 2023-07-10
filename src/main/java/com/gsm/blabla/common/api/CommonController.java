package com.gsm.blabla.common.api;

import com.gsm.blabla.common.application.CommonService;
import com.gsm.blabla.common.dto.KeywordDto;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.common.enums.Level;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 코드 관련 API")
@RestController
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @Operation(summary = "레벨 별 문구 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / eng")
    @GetMapping("{language}/common/levels")
    public DataResponseDto<Map<String, String>> getLevels(@PathVariable String language) {
        return DataResponseDto.of(commonService.getLevels(language));
    }

    @Operation(summary = "키워드 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / eng")
    @GetMapping("/{language}/common/keywords")
    public DataResponseDto<Map<String, List<KeywordDto>>> getKeywords(@PathVariable String language) {
        return DataResponseDto.of(commonService.getKeywords(language));
    }
}
