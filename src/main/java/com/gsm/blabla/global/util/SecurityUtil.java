package com.gsm.blabla.global.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {
    public static Long getCurrentMemberId() {
        // Request가 들어올 때 JwtFilter의 doFilter에서 저장
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        // memberId를 저장했기 때문에 Long 타입
        return Long.parseLong(authentication.getName());
    }
}
