package com.gsm.blabla.agora.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AgoraControllerTest extends ControllerTestSupport {

    @DisplayName("[POST] 보이스룸 입장을 위한 토큰을 발급 받는다.")
    @Test
    @WithCustomMockUser
    void create() throws Exception {
        // given
        long now = (new Date()).getTime();
        given(agoraService.create(any(VoiceRoomRequestDto.class)
            )
        )
            .willReturn(
                RtcTokenDto.builder()
                    .token("test")
                    .expiresIn(new Date(now + 3600).getTime())
                    .build()
            );

        // when // then
        mockMvc.perform(
            post("/crews/voice-room")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    VoiceRoomRequestDto.builder().isActivated(true).build()
                ))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.token").value("test"))
            .andExpect(jsonPath("$.data.expiresIn").isNumber())
            ;

    }
}
