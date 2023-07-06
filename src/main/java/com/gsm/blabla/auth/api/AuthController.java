package com.gsm.blabla.auth.api;

import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.global.common.Code;
import com.gsm.blabla.global.common.GeneralException;
import com.gsm.blabla.global.common.dto.DataResponseDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.application.MemberService;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.dto.MemberRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final MemberService memberService;

    /*
    * [POST] /oauth/sign-up
    * 회원가입 API
    * */
    @PostMapping("/sign-up")
    public DataResponseDto<Object> signup(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody MemberRequestDto memberRequestDto) {
        // Validation - 닉네임 중복 여부
        if (memberService.isNicknameDuplicate(memberRequestDto.getNickname())) {
            throw new GeneralException(Code.DUPLICATED_NICKNAME, "중복된 닉네임입니다.");
        }

        // Validation - 닉네임이 12자 이내인지
        if (memberRequestDto.getNickname().length() >= 12) {
            throw new GeneralException(Code.INVALID_NICKNAME_LENGTH, "닉네임은 12자 이내여야 합니다.");
        }

        // Validation - 스피킹 레벨이 1~5 이내인지
        if (!(levelInRange(memberRequestDto.getFirstLangLevel()) && levelInRange(memberRequestDto.getSecondLangLevel()))) {
            throw new GeneralException(Code.INVALID_LANG_LEVEL, "레벨은 1에서 5 사이여야 합니다");
        }
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

    private boolean levelInRange(int level) {
        return level >= 1 && level <= 5;
    }
}
