package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Crew 관련 API")
@RestController
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    @Operation(summary = "크루 생성 API")
    @PostMapping(value = "/crews")
    public DataResponseDto<Map<String, Long>> create(
        @RequestBody CrewRequestDto crewRequestDto) {
        return DataResponseDto.of(crewService.create(crewRequestDto));
    }

    @Operation(summary = "크루 단일 조회 API")
    @GetMapping(value = "/{language}/crews/{crewId}")
    public DataResponseDto<CrewResponseDto> get(
        @PathVariable("language") String language,
        @PathVariable("crewId") Long crewId) {
        return DataResponseDto.of(crewService.get(language, crewId));
    }

    // TODO: 크루 찾기 화면을 기준으로 제작되었으므로 홈 화면에서 어떻게 재활용할지 고민하기
    // TODO: page > lastCrewId로 수정
    @Operation(summary = "크루 목록 조회 API")
    @GetMapping(value = "/{language}/crews")
    public DataResponseDto<Page<CrewResponseDto>> getAll(
        @PathVariable("language") String language,
        @PageableDefault(sort = "id", direction = Direction.DESC)  Pageable pageable) {
        return DataResponseDto.of(crewService.getAll(language, pageable));
    }

    @Operation(summary = "나의 크루 조회 API")
    @GetMapping(value = "/crews/me")
    public DataResponseDto<List<CrewResponseDto>> getMyCrews() {
        return DataResponseDto.of(crewService.getMyCrews());
    }
}
