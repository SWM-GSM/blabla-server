package com.gsm.blabla.auth.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.jwt.application.JwtService;
import com.gsm.blabla.auth.dto.AppleAccountDto;
import com.gsm.blabla.auth.dto.AppleTokenDto;
import com.gsm.blabla.auth.dto.GoogleAccountDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberRequestDto;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private GoogleService googleService;

    @MockBean
    private AppleService appleService;

    @DisplayName("[POST] 한국어를 배우고 싶은 유저가 회원가입을 할 때,")
    @TestFactory
    Collection<DynamicTest> signUp() {
        String providerAuthorization = "test";

        // given
        given(jwtService.issueJwt(any(Member.class))).willReturn(JwtDto.builder()
                .grantType("Bearer")
                .accessToken("test")
                .refreshToken("test")
                .accessTokenExpiresIn(1234567890L)
                .refreshTokenExpiresIn(1234567890L)
            .build()
        );

        return List.of(
            DynamicTest.dynamicTest("Test로 진행한다.", () -> {
                //given
                MemberRequestDto memberRequestDto = createMemberRequestDto("TEST");
                long memberBeforeSignUp = memberRepository.count();

                // when
                JwtDto response = authService.signUp(providerAuthorization, memberRequestDto);

                // then
                Long memberAfterSignUp = memberRepository.count();
                assertThat(memberAfterSignUp).isEqualTo(memberBeforeSignUp + 1);
                assertTrue(memberRepository.findById(memberAfterSignUp).get().getNickname().matches("^[a-zA-Z]*$"));
                assertThat(response.getGrantType()).isEqualTo("Bearer");
                assertThat(response.getAccessToken()).isEqualTo("test");
                assertThat(response.getAccessToken()).isEqualTo("test");
                assertThat(response.getAccessTokenExpiresIn()).isEqualTo(1234567890L);
                assertThat(response.getRefreshTokenExpiresIn()).isEqualTo(1234567890L);

            }),

            DynamicTest.dynamicTest("Google로 진행한다.", () -> {
                //given
                MemberRequestDto memberRequestDto = createMemberRequestDto("GOOGLE");
                given(googleService.getGoogleAccountInfo(any(String.class))).willReturn(GoogleAccountDto.builder()
                    .id("test")
                    .email("test")
                    .verifiedEmail(true)
                    .name("test")
                    .givenName("스트")
                    .familyName("테")
                    .picture("www.test.com")
                    .locale("ko")
                    .build()
                );

                long googleAccountBeforeSignUp = googleAccountRepository.count();

                // when
                authService.signUp(providerAuthorization, memberRequestDto);

                // then
                assertThat(googleAccountRepository.count()).isEqualTo(googleAccountBeforeSignUp + 1);
            }),

            DynamicTest.dynamicTest("Apple로 진행한다.", () -> {
                //given
                MemberRequestDto memberRequestDto = createMemberRequestDto("APPLE");
                given(appleService.getAppleToken(providerAuthorization)).willReturn(AppleTokenDto.builder()
                        .accessToken("test")
                        .refreshToken("test")
                        .idToken("test")
                    .build()
                );
                given(appleService.getAppleAccount(any(String.class))).willReturn(AppleAccountDto.builder()
                        .iss("test")
                        .exp("test")
                        .iat("test")
                        .sub("test")
                        .atHash("test")
                        .email("test")
                        .emailVerified(true)
                        .isPrivateEmail(true)
                        .authTime("test")
                        .nonceSupported(true)
                        .build()
                );

                long appleAccountBeforeSignUp = appleAccountRepository.count();

                // when
                authService.signUp(providerAuthorization, memberRequestDto);

                // then
                assertThat(appleAccountRepository.count()).isEqualTo(appleAccountBeforeSignUp + 1);
            })
        );
    }

    MemberRequestDto createMemberRequestDto(String socialLoginType) {
        return MemberRequestDto.builder()
            .socialLoginType(socialLoginType)
            .learningLanguage("ko")
            .pushNotification(false)
            .build();
    }
}

