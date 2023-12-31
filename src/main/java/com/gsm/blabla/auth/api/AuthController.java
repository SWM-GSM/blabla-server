package com.gsm.blabla.auth.api;

import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.dto.MemberRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth 관련 API")
@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입 API")
    @PostMapping(value = "/sign-up", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public DataResponseDto<JwtDto> signUp(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody MemberRequestDto memberRequestDto) {
        return DataResponseDto.of(authService.signUp(providerAccessToken, memberRequestDto));
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login/{socialLoginType}")
    public DataResponseDto<Object> login(
        @PathVariable("socialLoginType") SocialLoginType socialLoginType,
        @RequestHeader("Authorization") String providerAccessToken
    ) {
        return DataResponseDto.of(authService.login(socialLoginType, providerAccessToken));
    }

    @Operation(summary = "refresh token 재발급 API")
    @PostMapping("/reissue")
    public DataResponseDto<Object> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return DataResponseDto.of(authService.reissue(tokenRequestDto));
    }
}
