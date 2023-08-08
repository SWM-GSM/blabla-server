package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.ScheduleService;
import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.crew.dto.ScheduleResponseDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "크루 스페이스 일정 관련 API")
@RestController
@RequestMapping("/crews/{crewId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "크루 일정 생성 API")
    @PostMapping(value = "")
    public DataResponseDto<Map<String, Long>> create(
        @PathVariable Long crewId,
        @RequestBody ScheduleRequestDto scheduleRequestDto
        ) {
        return DataResponseDto.of(scheduleService.create(crewId, scheduleRequestDto));
    }

    @Operation(summary = "크루 일정 전체 조회 API")
    @GetMapping(value = "")
    public DataResponseDto<Map<String, List<ScheduleResponseDto>>> getAll(@PathVariable Long crewId) {
        return DataResponseDto.of(scheduleService.getAll(crewId));
    }

    @Operation(summary = "다가오는 크루 일정 API")
    @GetMapping(value = "/upcoming")
    public DataResponseDto<ScheduleResponseDto> getUpcomingSchedule(@PathVariable Long crewId) {
        return DataResponseDto.of(scheduleService.getUpcomingSchedule(crewId));
    }

    @Operation(summary = "크루 일정 참여하기 API")
    @PostMapping(value = "/join")
    public DataResponseDto<Map<String, String>> joinSchedule(
        @PathVariable Long crewId,
        @RequestBody ScheduleRequestDto scheduleRequestDto
        ) {
        return DataResponseDto.of(scheduleService.joinSchedule(crewId, scheduleRequestDto));
    }

    @Operation(summary = "크루 일정 참여 취소 API")
    @DeleteMapping(value = "")
    public DataResponseDto<Map<String, String>> cancelSchedule(
        @PathVariable Long crewId,
        @RequestBody ScheduleRequestDto scheduleRequestDto
        ) {
        return DataResponseDto.of(scheduleService.cancelSchedule(crewId, scheduleRequestDto));
    }
}
