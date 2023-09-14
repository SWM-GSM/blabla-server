package com.gsm.blabla.agora.application;

import com.gsm.blabla.agora.RtcTokenBuilder2;
import com.gsm.blabla.agora.RtcTokenBuilder2.Role;
import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.Date;
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

    private final CrewReportRepository crewReportRepository;

    static final int TOKEN_EXPIRATION_TIME = 1000 * 60 * 30;
    static final int PRIVILEGE_EXPIRATION_TIME = 1000 * 60 * 30;

    public RtcTokenDto create(VoiceRoomRequestDto voiceRoomRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        String channelName = "blablah";
        RtcTokenBuilder2 token = new RtcTokenBuilder2();
        long now = (new Date()).getTime();

        boolean isActivated = voiceRoomRequestDto.getIsActivated();
        if (!isActivated) {
             crewReportRepository.save(
                CrewReport.builder()
                    .startedAt(LocalDateTime.now())
                    .build()
             );
        }

        return RtcTokenDto.builder()
            .token(token.buildTokenWithUid(appId, appCertificate, channelName, memberId,
                Role.ROLE_PUBLISHER, TOKEN_EXPIRATION_TIME, PRIVILEGE_EXPIRATION_TIME))
            .expiresIn(new Date(now + TOKEN_EXPIRATION_TIME).getTime())
            .reportId(crewReportRepository.findCurrentId())
            .build();
    }
}
