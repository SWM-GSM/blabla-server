package com.gsm.blabla.agora.application;

import com.gsm.blabla.agora.RtcTokenBuilder2;
import com.gsm.blabla.agora.RtcTokenBuilder2.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AgoraService {
    @Value("${agora.app-id}")
    private String appId;
    @Value("${agora.app-certificate}")
    private String appCertificate;
    // TODO: 유효 기간 변경하기
    static final int TOKEN_EXPIRATION_IN_SECONDS = 3600; // 1 hour
    static final int PRIVILEGE_EXPIRATION_IN_SECONDS = 3600; // 1 hour

    public String create(String channelName, Long uid) {
        RtcTokenBuilder2 token = new RtcTokenBuilder2();

        // TODO: channelName null check
        return token.buildTokenWithUid(appId, appCertificate, channelName, uid,
            Role.ROLE_PUBLISHER, TOKEN_EXPIRATION_IN_SECONDS, PRIVILEGE_EXPIRATION_IN_SECONDS);
    }
}
