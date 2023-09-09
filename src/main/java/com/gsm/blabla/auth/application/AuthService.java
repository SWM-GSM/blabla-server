package com.gsm.blabla.auth.application;

import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.jwt.TokenProvider;
import com.gsm.blabla.jwt.application.JwtService;
import com.gsm.blabla.auth.dao.AppleAccountRepository;
import com.gsm.blabla.auth.dao.GoogleAccountRepository;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.auth.domain.AppleAccount;
import com.gsm.blabla.auth.domain.GoogleAccount;
import com.gsm.blabla.jwt.domain.Jwt;
import com.gsm.blabla.jwt.dto.AppleAccountDto;
import com.gsm.blabla.jwt.dto.AppleTokenDto;
import com.gsm.blabla.jwt.dto.GoogleAccountDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.domain.nickname.Adjective;
import com.gsm.blabla.member.domain.nickname.Animal;
import com.gsm.blabla.member.domain.nickname.Color;
import com.gsm.blabla.member.dto.MemberRequestDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final TokenProvider tokenProvider;
    private final JwtService jwtService;
    private final GoogleService googleService;
    private final AppleService appleService;
    private final MemberRepository memberRepository;
    private final JwtRepository jwtRepository;
    private final GoogleAccountRepository googleAccountRepository;
    private final AppleAccountRepository appleAccountRepository;

    // 회원가입
    public JwtDto signUp(String providerAuthorization, MemberRequestDto memberRequestDto) {
        Member member = new Member();

        // 랜덤으로 닉네임, 프로필 이미지 생성
        String nickname = "";

        Animal animal = Animal.getRandomAnimal();
        String profileImage = animal.getEnglishName();

        String learningLanguage = memberRequestDto.getLearningLanguage();

        if (learningLanguage.equals("ko")) {
            nickname = Adjective.getRandomAdjective("ko") + Color.getRandomColor("ko") + animal.getKoreanName();
        } else if (learningLanguage.equals("en")) {
            nickname = Adjective.getRandomAdjective("en") + Color.getRandomColor("en") + animal.getEnglishName();
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            long memberId = memberRepository.findLastId() + 1;
            nickname = nickname + memberId;
        }

        switch (memberRequestDto.getSocialLoginType()) {
            case "GOOGLE" -> {
                GoogleAccountDto googleAccountDto = googleService.getGoogleAccountInfo(providerAuthorization);
                if (googleAccountRepository.findById(googleAccountDto.getId()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, "이미 가입된 구글 계정입니다.");
                }

                member = memberRepository.save(memberRequestDto.toEntity(nickname, profileImage));
                googleAccountRepository.save(GoogleAccount.builder()
                    .id(googleAccountDto.getId())
                    .member(member)
                    .build()
                );

            }
            case "APPLE" -> {
                AppleTokenDto appleTokenDto = appleService.getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = appleService.getAppleAccount(appleTokenDto.getIdToken());
                if (appleAccountRepository.findById(appleAccountDto.getSub()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, "이미 가입된 애플 계정입니다.");
                }

                member = memberRepository.save(memberRequestDto.toEntity(nickname, profileImage));
                appleAccountRepository.save(AppleAccount.builder()
                    .id(appleAccountDto.getSub())
                    .member(member)
                    .refreshToken(appleTokenDto.getRefreshToken())
                    .build()
                );
            }
            case "TEST" -> member = memberRepository.save(memberRequestDto.toEntity(nickname, profileImage));
        }

        return jwtService.issueJwt(member);
    }

    // 로그인
    public Object login(SocialLoginType socialLoginType, String providerAuthorization) {
        Optional<Member> member = Optional.empty();

        switch (socialLoginType) {
            case GOOGLE -> {
                GoogleAccountDto googleAccountDto = googleService.getGoogleAccountInfo(providerAuthorization);
                member = googleAccountRepository.findById(googleAccountDto.getId()).map(GoogleAccount::getMember);
                if (member.isEmpty()) {
                    throw new GeneralException(Code.MEMBER_NOT_FOUND, "가입되지 않은 유저입니다.");
                }
            }
            case APPLE -> {
                AppleTokenDto appleTokenDto = appleService.getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = appleService.getAppleAccount(appleTokenDto.getIdToken());
                member = appleAccountRepository.findById(appleAccountDto.getSub()).map(AppleAccount::getMember);
                if (member.isEmpty()) {
                    throw new GeneralException(Code.MEMBER_NOT_FOUND, "가입되지 않은 유저입니다.");
                }
            }
        }

        return jwtService.issueJwt(member.get());
    }

    // Refresh Token 재발급
    public JwtDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN, "Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        Long memberId = Long.parseLong(authentication.getName());

        Jwt jwt = jwtRepository.findOneByMemberId(memberId)
            .orElseThrow(() -> new GeneralException(Code.REFRESH_TOKEN_NOT_FOUND, "Refresh Token이 없습니다. 다시 로그인해주세요."));
        if (!jwt.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN, "Refresh Token이 유효하지 않습니다.");
        }

        jwtRepository.delete(jwt);
        jwtRepository.flush();

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        JwtDto newJwtDto = tokenProvider.generateJwt(member);
        Jwt newJwt = Jwt.builder()
            .member(member)
            .refreshToken(newJwtDto.getRefreshToken())
            .build();
        jwtRepository.save(newJwt);

        // 토큰 발급
        return newJwtDto;
    }

        }

    }
}
