package com.gsm.blabla.crew.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    String meetingTime = LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @DisplayName("[POST] 크루 스페이스 일정을 성공적으로 생성한다.")
    @Test
    @WithCustomMockUser
    void create() throws Exception {
        // given
        given(scheduleService.createSchedule(any(ScheduleRequestDto.class)))
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
                        .meetingTime(meetingTime)
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
        given(scheduleService.getAllSchedule()).willReturn(Collections.singletonMap("schedules", List.of(schedule1, schedule2, schedule3)));

        // when // then
        mockMvc.perform(
            get("/crews/schedules")
                .header("Authorization", "test")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.schedules", hasSize(3)));
    }

    @DisplayName("[GET] 다가오는 크루 스페이스 일정을 조회한다.")
    @Test
    @WithCustomMockUser
    void getUpcomingSchedule() throws Exception {
        // given
        given(scheduleService.getUpcomingSchedule()).willReturn(
            ScheduleResponseDto.builder()
                .id(1L)
                .title("테스트")
                .meetingTime(meetingTime)
                .profiles(List.of("dog", "lion"))
                .dDay(3)
                .build()
        );

        // when // then
        mockMvc.perform(
            get("/crews/schedules/upcoming")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.title").value("테스트"))
            .andExpect(jsonPath("$.data.dday").value(3))
            .andExpect(jsonPath("$.data.meetingTime").value(meetingTime))
            .andExpect(jsonPath("$.data.profiles", hasSize(2)));
    }

    @DisplayName("[POST] 크루 스페이스 일정에 참여한다.")
    @Test
    @WithCustomMockUser
    void joinSchedule() throws Exception {
        // given
        given(scheduleService.joinSchedule(any(ScheduleRequestDto.class)))
            .willReturn(Collections.singletonMap("message", "일정 참여가 완료되었습니다."));

        // when // then
        mockMvc.perform(
            post("/crews/schedules/join")
                .with(csrf())
                .header("Authorization", "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                    ScheduleRequestDto.builder()
                        .scheduleId(any(Long.class))
                        .build()
                ))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.message").value("일정 참여가 완료되었습니다."));
    }

    @DisplayName("[DELETE] 크루 스페이스 일정 참여를 취소한다.")
    @Test
    @WithCustomMockUser
    void cancelSchedule() throws Exception {
        // given
        given(scheduleService.cancelSchedule(any(ScheduleRequestDto.class)))
            .willReturn(Collections.singletonMap("message", "일정 참여가 취소되었습니다."));

        // when // then
        mockMvc.perform(
            delete("/crews/schedules")
                .with(csrf())
                .header("Authorization", "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                    ScheduleRequestDto.builder()
                        .scheduleId(any(Long.class))
                        .build()
                ))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.message").value("일정 참여가 취소되었습니다."));
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
