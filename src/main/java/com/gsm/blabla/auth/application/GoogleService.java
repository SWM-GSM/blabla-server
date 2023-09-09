package com.gsm.blabla.auth.application;

import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.auth.dao.GoogleAccountRepository;
import com.gsm.blabla.auth.dto.GoogleAccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleService {

    private final RestTemplate restTemplate;
    private final GoogleAccountRepository googleAccountRepository;

    public GoogleAccountDto getGoogleAccountInfo(String googleAccessToken) {
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

    public void unlinkGoogleAccount(Long memberId) {
        googleAccountRepository.deleteByMemberId(memberId);
    }
}
