package com.gsm.blabla.agora.application;

import com.gsm.blabla.agora.RtcTokenBuilder2;
import com.gsm.blabla.agora.RtcTokenBuilder2.Role;
import com.gsm.blabla.agora.dao.VoiceRoomRepository;
import com.gsm.blabla.agora.domain.VoiceRoom;
import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final MemberRepository memberRepository;
    private final VoiceRoomRepository voiceRoomRepository;
    private final VoiceFileRepository voiceFileRepository;

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
                    .member(memberRepository.findById(memberId).orElseThrow(
                        () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
                    ))
                    .build()
             );
        }

        boolean inVoiceRoom = voiceRoomRepository.existsByMemberId(memberId);
        if (inVoiceRoom) {
            throw new GeneralException(Code.ALREADY_IN_VOICE_ROOM, "이미 보이스 채팅방에 입장하셨습니다.");
        } else {
            voiceRoomRepository.save(
                VoiceRoom.builder()
                    .member(memberRepository.findById(memberId).orElseThrow(
                        () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
                    ))
                    .inVoiceRoom(true)
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

    public Map<String, List<MemberResponseDto>> getMembers() {
        List<MemberResponseDto> membersInVoiceRoom = voiceRoomRepository.findAllByInVoiceRoom(true).stream()
            .map(VoiceRoom::getMember)
            .map(MemberResponseDto::voiceRoomResponse)
            .toList();

        return Collections.singletonMap("members", membersInVoiceRoom);
    }

    public Map<String, List<MemberResponseDto>> getPreviousMembers(Long reportId) {

        List<MemberResponseDto> membersInVoiceRoom = voiceFileRepository.findAllByCrewReportId(reportId)
                .stream()
                .map(VoiceFile::getMember)
                .map(MemberResponseDto::voiceRoomResponse)
                .collect(Collectors.toMap(MemberResponseDto::getId, Function.identity(), (m1, m2) -> m1))
                .values().stream()
                .toList();

        return Collections.singletonMap("previousMembers", membersInVoiceRoom);
    }
}
