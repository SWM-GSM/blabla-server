package com.gsm.blabla.crew.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.crew.dto.ScheduleResponseDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ScheduleControllerTest extends ControllerTestSupport {

    @DisplayName("[POST] 유저가 크루 스페이스 일정을 성공적으로 생성한다.")
    @Test
    @WithCustomMockUser
    void create() throws Exception {
        // given
        given(scheduleService.create(any(ScheduleRequestDto.class)))
            .willReturn(Collections.singletonMap("scheduleId", 1L));

        // when // then
        mockMvc.perform(
            post("/crews/schedules")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "test")
                .content(objectMapper.writeValueAsString(
                    ScheduleRequestDto.builder()
                        .title("test")
                        .meetingTime(LocalDateTime.now().plusDays(3)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build()
                ))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.scheduleId").value(1L));
    }

    @DisplayName("[GET] 크루 스페이스 일정을 전체 조회한다.")
    @Test
    @WithCustomMockUser
    void getAll() throws Exception {
        // given
        ScheduleResponseDto schedule1 = createScheduleResponseDto(1L, "테스트1", "2023-01-01 00:00:00", 0, "ENDED");
        ScheduleResponseDto schedule2 = createScheduleResponseDto(2L, "테스트2", "2023-01-02 00:00:00", 0, "ENDED");
        ScheduleResponseDto schedule3 = createScheduleResponseDto(3L, "테스트3", "2023-01-03 00:00:00", 0, "ENDED");
        given(scheduleService.getAll()).willReturn(Collections.singletonMap("schedules", List.of(schedule1, schedule2, schedule3)));

        // when // then
        mockMvc.perform(
            get("/crews/schedules")
                .header("Authorization", "test")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.schedules", hasSize(3)));
        ;
    }

    ScheduleResponseDto createScheduleResponseDto(Long id, String title, String meetingTime, Integer dday, String status) {
        return ScheduleResponseDto.builder()
            .id(id)
            .title(title)
            .meetingTime(meetingTime)
            .members(List.of(
                MemberResponseDto.builder().id(1L).nickname("테스트").profileImage("dog").build(),
                MemberResponseDto.builder().id(2L).nickname("테스트2").profileImage("lion").build()
            ))
            .dDay(dday)
            .status(status)
            .build();
    }
}
