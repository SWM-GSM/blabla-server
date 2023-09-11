package com.gsm.blabla.crew.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ScheduleControllerTest extends ControllerTestSupport {

    @DisplayName("[POST] 유저가 크루 스페이스 스케줄을 성공적으로 생성한다.")
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
}
