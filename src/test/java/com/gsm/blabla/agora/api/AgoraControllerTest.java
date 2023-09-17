package com.gsm.blabla.agora.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @DisplayName("[GET] 보이스룸에 접속한 유저 목록을 조회한다.")
    @Test
    @WithCustomMockUser
    void getMembers() throws Exception {
        // given
        given(agoraService.getMembers())
            .willReturn(Map.of("members",
                List.of(
                    MemberResponseDto.builder().profileImage("dog").nickname("test1").build(),
                    MemberResponseDto.builder().profileImage("lion").nickname("test2").build(),
                    MemberResponseDto.builder().profileImage("wolf").nickname("test3").build()
                )
                ));

        // when // then
        mockMvc.perform(
            get("/crews/voice-room")

        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.members", hasSize(3)))
            .andExpect(jsonPath("$.data.members[0].nickname").value("test1"))
            .andExpect(jsonPath("$.data.members[1].nickname").value("test2"))
            .andExpect(jsonPath("$.data.members[2].nickname").value("test3"));
    }
}
