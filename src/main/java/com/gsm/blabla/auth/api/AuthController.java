package com.gsm.blabla.auth.api;

import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.global.common.dto.DataResponseDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.dto.MemberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /*
    * [POST] /oauth/sign-up
    * 회원가입 API
    * */
    @PostMapping("/sign-up")
    public DataResponseDto<Object> signup(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody MemberRequestDto memberRequestDto) {
        return DataResponseDto.of(authService.signup(providerAccessToken, memberRequestDto));
    }

    /*
    * [POST] /oauth/login
    * 로그인 API
    * */
    @PostMapping("/login/{socialLoginType}")
    public DataResponseDto<Object> login(
        @PathVariable("socialLoginType") SocialLoginType socialLoginType,
        @RequestHeader("Authorization") String providerAccessToken
    ) {
        return DataResponseDto.of(authService.login(socialLoginType, providerAccessToken));
    }

    /*
    * [POST] /oauth/reissue
    * refresh token 재발급 API
    * */
    @PostMapping("/reissue")
    public DataResponseDto<Object> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return DataResponseDto.of(authService.reissue(tokenRequestDto));
    }
}
