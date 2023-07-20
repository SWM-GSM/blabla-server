package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.ScheduleService;
import com.gsm.blabla.crew.dto.CrewScheduleRequestDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CrewSchedule 관련 API")
@RestController
@RequestMapping("/crews/{crewId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "크루 일정 생성 API")
    @PostMapping(value = "")
    public DataResponseDto<Map<String, Long>> create(
        @PathVariable Long crewId,
        @RequestBody CrewScheduleRequestDto crewScheduleRequestDto
        ) {
        return DataResponseDto.of(scheduleService.create(crewId, crewScheduleRequestDto));
    }
}
