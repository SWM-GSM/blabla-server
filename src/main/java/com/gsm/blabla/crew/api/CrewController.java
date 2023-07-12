package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Crew 관련 API")
@RestController
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    @Operation(summary = "크루 생성 API")
    @PostMapping(value = "/crews", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public DataResponseDto<Map<String, Long>> create(
        @RequestPart CrewRequestDto crewRequestDto,
        @RequestPart(required = false) MultipartFile coverImage) {
        return DataResponseDto.of(crewService.create(crewRequestDto, coverImage));
    }

    @Operation(summary = "크루 단일 조회 API")
    @GetMapping(value = "/{language}/crews/{crewId}")
    public DataResponseDto<Map<String, CrewResponseDto>> get(
        @PathVariable("language") String language,
        @PathVariable("crewId") Long crewId) {
        return DataResponseDto.of(crewService.get(language, crewId));
    }

}
