package com.gsm.blabla.auth.application;

import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.jwt.TokenProvider;
import com.gsm.blabla.jwt.application.JwtService;
import com.gsm.blabla.jwt.dao.GoogleAccountRepository;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.jwt.domain.GoogleAccount;
import com.gsm.blabla.jwt.domain.Jwt;
import com.gsm.blabla.jwt.dto.GoogleAccountDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.dto.MemberRequestDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final JwtRepository jwtRepository;
    private final GoogleAccountRepository googleAccountRepository;

    // 회원가입
    public JwtDto signup(String providerAuthorization, MemberRequestDto memberRequestDto) {
        Member member = new Member();

        if (memberRepository.findByNickname(memberRequestDto.getNickname()).isPresent()) {
            throw new GeneralException(Code.DUPLICATED_NICKNAME, "중복된 닉네임입니다.");
        }

        switch (memberRequestDto.getSocialLoginType()) {
            case "GOOGLE" -> {
                GoogleAccountDto googleAccountDto = getGoogleAccountInfo(providerAuthorization);

                member = memberRepository.save(memberRequestDto.toEntity());

                googleAccountRepository.save(GoogleAccount.builder()
                    .id(googleAccountDto.getId())
                    .member(member)
                    .build());

            }
            case "APPLE" -> {
                // TODO: 추후 구현
            }
        }

        // 키워드
        for (Keyword keyword : memberRequestDto.getKeywords()) {
            memberKeywordRepository.save(MemberKeyword.builder()
                    .member(member)
                    .keyword(keyword)
                    .build());
        }

        return jwtService.issueJwt(member);
    }

    // 로그인
    public Object login(SocialLoginType socialLoginType, String providerAuthorization) {
        Optional<Member> member = Optional.empty();

        // 가입된 회원 - access / refresh token 발급
        // 가입되지 않은 회원 - provider token으로 얻은 정보 반환
        switch (socialLoginType) {
            case GOOGLE -> {
                GoogleAccountDto googleAccountDto = getGoogleAccountInfo(providerAuthorization);
                member = googleAccountRepository.findById(googleAccountDto.getId()).map(GoogleAccount::getMember);
                if (member.isEmpty()) {
                    return googleAccountDto.toMemberRequestDto();
                }
            }
            case APPLE -> {
                // TODO: 추후 구현
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

    // Google Access Token에서 정보 가져오기
    private GoogleAccountDto getGoogleAccountInfo(String googleAccessToken) {
        String GOOGLE_USERINFO_REQUEST_URL="https://www.googleapis.com/oauth2/v1/userinfo";

        // 1. header에 Access Token을 담는다
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + googleAccessToken);

        // 2. HttpEntity를 생성해 헤더에 담아서 restTemplate으로 구글과 통신한다
        HttpEntity request = new HttpEntity(null, headers);

        ResponseEntity<GoogleAccountDto> response;
        try {
            response = restTemplate.exchange(
                GOOGLE_USERINFO_REQUEST_URL,
                HttpMethod.GET,
                request,
                GoogleAccountDto.class
            );
        } catch (RestClientException e) {
            throw new GeneralException(Code.GOOGLE_SERVER_ERROR);
        }

        return response.getBody();
    }
}
