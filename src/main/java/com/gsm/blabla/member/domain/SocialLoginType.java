package com.gsm.blabla.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialLoginType {
    GOOGLE,
    APPLE,
    FACEBOOK,
    KAKAO;
}
