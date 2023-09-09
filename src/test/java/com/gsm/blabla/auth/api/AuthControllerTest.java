package com.gsm.blabla.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.member.dto.MemberRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("[POST] TEST 타입으로 회원가입을 성공한다.")
    @Test
    @WithCustomMockUser
    void signUp() throws Exception {
        // given
        given(authService.signUp(any(String.class), any(MemberRequestDto.class)))
            .willReturn(JwtDto.builder()
                .grantType("Bearer")
                .accessToken("test")
                .refreshToken("test")
                .accessTokenExpiresIn(1234567890L)
                .refreshTokenExpiresIn(1234567890L)
                .build());

        // when // then
        mockMvc.perform(
            post("/oauth/sign-up")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "test")
                .content(objectMapper.writeValueAsString(
                    MemberRequestDto.builder()
                        .socialLoginType("TEST")
                        .learningLanguage("ko")
                        .pushNotification(false)
                        .build()
                ))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.grantType").value("Bearer"))
            .andExpect(jsonPath("$.data.accessToken").value("test"))
            .andExpect(jsonPath("$.data.refreshToken").value("test"))
            .andExpect(jsonPath("$.data.accessTokenExpiresIn").value(1234567890L))
            .andExpect(jsonPath("$.data.refreshTokenExpiresIn").value(1234567890L));
    }
}
