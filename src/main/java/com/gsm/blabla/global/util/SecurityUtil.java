package com.gsm.blabla.global.util;

import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Long getMemberId() {
        // Request가 들어올 때 JwtFilter의 doFilter에서 저장
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            throw new GeneralException(Code.NO_CREDENTIALS_IN_CONETEXT, "Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
