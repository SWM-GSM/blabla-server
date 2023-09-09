package com.gsm.blabla.auth.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.auth.dao.AppleAccountRepository;
import com.gsm.blabla.jwt.dto.AppleAccountDto;
import com.gsm.blabla.jwt.dto.ApplePublicKeyDto;
import com.gsm.blabla.jwt.dto.AppleTokenDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class AppleService {

    private final AppleAccountRepository appleAccountRepository;

    @Value("${spring.security.oauth2.provider.apple.team-id}")
    private String appleTeamId;
    @Value("${spring.security.oauth2.provider.apple.client-id}")
    private String appleClientId;
    @Value("${spring.security.oauth2.provider.apple.key-id}")
    private String appleKeyId;
    @Value("${spring.security.oauth2.provider.apple.private-key}")
    private String applePrivateKey;

    public AppleAccountDto getAppleAccount(String identityToken) {
        // public key 구성요소를 조회한 뒤 JWT의 서명을 검증한 후 Claim을 응답한다
        try {
            identityToken = identityToken.replace("Bearer ", "");

            ApplePublicKeyDto applePublicKey = new RestTemplate().exchange(
                "https://appleid.apple.com/auth/keys",
                HttpMethod.GET,
                new HttpEntity<>(null, null),
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

    public AppleTokenDto getAppleToken(String appleAuthorizationCode) {
        try {
            appleAuthorizationCode = appleAuthorizationCode.replace("Bearer ", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
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
        Date expirationDate = Date.from(
            LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
            .setHeaderParam("kid", appleKeyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(appleTeamId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .setAudience("https://appleid.apple.com")
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

    public void revokeAppleAccount(Long memberId) {
        try {
            String appleRefreshToken = appleAccountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")).getRefreshToken();

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            params.add("client_id", appleClientId);
            params.add("client_secret", getAppleClientSecret());
            params.add("token", appleRefreshToken);
            params.add("token_type_hint", "refresh_token");

            new RestTemplate().exchange(
                "https://appleid.apple.com/auth/revoke",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                String.class
            );

            appleAccountRepository.deleteByMemberId(memberId);
        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, e);
        }
    }
}
