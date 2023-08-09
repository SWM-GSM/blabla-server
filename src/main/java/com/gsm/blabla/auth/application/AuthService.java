package com.gsm.blabla.auth.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.jwt.TokenProvider;
import com.gsm.blabla.jwt.application.JwtService;
import com.gsm.blabla.jwt.dao.AppleAccountRepository;
import com.gsm.blabla.jwt.dao.GoogleAccountRepository;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.jwt.domain.AppleAccount;
import com.gsm.blabla.jwt.domain.GoogleAccount;
import com.gsm.blabla.jwt.domain.Jwt;
import com.gsm.blabla.jwt.dto.AppleAccountDto;
import com.gsm.blabla.jwt.dto.ApplePublicKeyDto;
import com.gsm.blabla.jwt.dto.AppleTokenDto;
import com.gsm.blabla.jwt.dto.GoogleAccountDto;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.jwt.dto.TokenRequestDto;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.dto.MemberRequestDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final AppleAccountRepository appleAccountRepository;

    @Value("${spring.security.oauth2.provider.apple.team-id}")
    private String appleTeamId;
    @Value("${spring.security.oauth2.provider.apple.client-id}")
    private String appleClientId;
    @Value("${spring.security.oauth2.provider.apple.key-id}")
    private String appleKeyId;
    @Value("${spring.security.oauth2.provider.apple.private-key}")
    private String applePrivateKey;

    // 회원가입
    public JwtDto signup(String providerAuthorization, MemberRequestDto memberRequestDto) {
        Member member = new Member();

        if (memberRepository.findByNickname(memberRequestDto.getNickname()).isPresent()) {
            throw new GeneralException(Code.DUPLICATED_NICKNAME, "중복된 닉네임입니다.");
        }

        // TODO: 이미 가입한 유저 예외 처리

        switch (memberRequestDto.getSocialLoginType()) {
            case "GOOGLE" -> {
                GoogleAccountDto googleAccountDto = getGoogleAccountInfo(providerAuthorization);
                if (googleAccountRepository.findById(googleAccountDto.getId()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, "이미 가입된 구글 계정입니다.");
                }

                member = memberRepository.save(memberRequestDto.toEntity());
                googleAccountRepository.save(GoogleAccount.builder()
                    .id(googleAccountDto.getId())
                    .member(member)
                    .build());

            }
            case "APPLE" -> {
                AppleTokenDto appleTokenDto = getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = getAppleAccount(appleTokenDto.getIdToken());
                if (appleAccountRepository.findById(appleTokenDto.getIdToken()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, "이미 가입된 애플 계정입니다.");
                }

                member = memberRepository.save(memberRequestDto.toEntity());
                appleAccountRepository.save(AppleAccount.builder()
                    .id(appleAccountDto.getSub())
                    .member(member)
                    .refreshToken(appleTokenDto.getRefreshToken())
                    .build()
                );
            }
            case "TEST" -> member = memberRepository.save(memberRequestDto.toEntity());
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

        switch (socialLoginType) {
            case GOOGLE -> {
                GoogleAccountDto googleAccountDto = getGoogleAccountInfo(providerAuthorization);
                member = googleAccountRepository.findById(googleAccountDto.getId()).map(GoogleAccount::getMember);
                if (member.isEmpty()) {
                    throw new GeneralException(Code.MEMBER_NOT_FOUND, "가입되지 않은 유저입니다.");
                }
            }
            case APPLE -> {
                AppleTokenDto appleTokenDto = getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = getAppleAccount(appleTokenDto.getIdToken());
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

    private AppleAccountDto getAppleAccount(String identityToken) {
        // public key 구성요소를 조회한 뒤 JWT의 서명을 검증한 후 Claim을 응답한다
        try {
            identityToken = identityToken.replace("Bearer ", "");

            ApplePublicKeyDto applePublicKey = new RestTemplate().exchange(
                "https://appleid.apple.com/auth/keys",
                HttpMethod.GET,
                null,
                ApplePublicKeyDto.class
            ).getBody();

            String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));

            Map<String, String> header = new ObjectMapper().readValue(
                new String(Base64Utils.decodeFromUrlSafeString(headerOfIdentityToken), StandardCharsets.UTF_8),
                Map.class
            );
            ApplePublicKeyDto.Key key = applePublicKey.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                .orElseThrow(() -> new NullPointerException("Failed to get public key from apple's id server"));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Claims memberInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();

            Map<String, Object> expectedMap = new HashMap<>(memberInfo);

            return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .convertValue(expectedMap, AppleAccountDto.class);

        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, "apple server error");
        }
    }

    private AppleTokenDto getAppleToken(String appleAuthorizationCode) {
        try {
            appleAuthorizationCode = appleAuthorizationCode.replace("Bearer ", "");

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            params.add("client_id", appleClientId);
            params.add("client_secret", getAppleClientSecret());
            params.add("code", appleAuthorizationCode);
            params.add("grant_type", "authorization_code");

            return new RestTemplate().exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                AppleTokenDto.class
            ).getBody();
        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, e);
        }
    }

    private String getAppleClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
            .setHeaderParam("kid", appleKeyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(appleTeamId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .setAudience("https://appleid.apple.com")
            .setSubject(appleClientId)
            .setSubject(appleClientId)
            .signWith(SignatureAlgorithm.ES256, getApplePrivateKey())
            .compact();
    }

    private PrivateKey getApplePrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(applePrivateKey);
        String privateKey = new String(resource.getInputStream().readAllBytes());
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}
