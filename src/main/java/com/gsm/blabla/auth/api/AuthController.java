package com.gsm.blabla.auth.api;

import com.gsm.blabla.auth.application.AuthService;
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
    public ResponseEntity<JwtDto> signup(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signup(providerAccessToken, memberRequestDto));
    }

    /*
    * [POST] /oauth/login
    * 로그인 API
    * */
    @PostMapping("/login/{socialLoginType}")
    public ResponseEntity<Object> login(
        @PathVariable("socialLoginType") SocialLoginType socialLoginType,
        @RequestHeader("Authorization") String providerAccessToken
    ) {
        return ResponseEntity.ok(authService.login(socialLoginType, providerAccessToken));
    }

    /*
    * [POST] /oauth/reissue
    * refresh token 재발급 API
    * */
    @PostMapping("/reissue")
    public ResponseEntity<JwtDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
