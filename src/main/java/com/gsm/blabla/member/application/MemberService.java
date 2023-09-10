package com.gsm.blabla.member.application;

import com.gsm.blabla.auth.application.AppleService;
import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.auth.application.GoogleService;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.member.dao.MemberRepository;

import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtRepository jwtRepository;

    private final AuthService authService;
    private final GoogleService googleService;
    private final AppleService appleService;

    @Transactional(readOnly = true)
    public Map<String, Boolean> isNicknameDuplicated(String nickname) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isDuplicated", memberRepository.findByNickname(nickname).isPresent());

        return result;
    }

    public Map<String, String> withdrawal() {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        jwtRepository.deleteByMemberId(memberId);
        memberRepository.delete(member);

        switch (member.getSocialLoginType()) {
            case GOOGLE -> {
                googleService.unlinkGoogleAccount(memberId);
            }
            case APPLE -> {
                appleService.revokeAppleAccount(memberId);
            }
        }

        log.info("{} 님이 회원 탈퇴를 하였습니다.", member.getNickname());
        return Collections.singletonMap("message", "회원탈퇴가 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public MemberProfileResponseDto getProfile(String language) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        return MemberProfileResponseDto.getMemberProfile(member);
    }

    public Map<String, String> updatePushNotification(PushNotificationRequestDto pushNotificationRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.setPushNotification(pushNotificationRequestDto.getPushNotification());

        return Collections.singletonMap("message", "푸시 알림 설정이 완료되었습니다.");
    }

    public Map<String, String> updateProfile(MemberProfileRequestDto memberProfileRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.updateMember(memberProfileRequestDto);

        return Collections.singletonMap("message", "프로필 수정이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public Map<String, Map<String, Boolean>> getSettings() {
        Long memberId = SecurityUtil.getMemberId();

        boolean pushNotification = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        ).getPushNotification();

        Map<String, Boolean> settings = Map.of("pushNotification", pushNotification);

        return Collections.singletonMap("settings", settings);
    }

    public Map<String, Long> getMyId() {
        return Collections.singletonMap("id", SecurityUtil.getMemberId());
    }

    public Map<String, List<MemberResponseDto>> getInfosFromIds(MemberRequestDto memberRequestDto) {
        List<MemberResponseDto> members = memberRequestDto.getIds().stream()
            .map(id -> memberRepository.findById(id)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."))
            )
            .map(member -> MemberResponseDto.builder()
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .build()
            )
            .toList();


        return Collections.singletonMap("members", members);
    }
}
