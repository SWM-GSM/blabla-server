package com.gsm.blabla.member.application;

import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;

import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final JwtRepository jwtRepository;

    private final AuthService authService;

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
                authService.unlinkGoogleAccount(memberId);
            }
            case APPLE -> {
                authService.revokeAppleAccount(memberId);
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

        List<MemberKeyword> memberInterest = memberKeywordRepository.findAllByMemberId(memberId);

        List<Keyword> keywords = memberInterest.stream()
                .map(MemberKeyword::getKeyword)
                .toList();

        List<Map<String, String>> interests = keywords.stream()
                .map(keyword -> {
                    Map<String, String> interest = new HashMap<>();
                    if ("ko".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getKoreanName());
                        interest.put("tag", keyword.name());
                    } else if ("en".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getEnglishName());
                        interest.put("tag", keyword.name());
                    }
                    return interest;
                })
                .toList();


        return MemberProfileResponseDto.getMemberProfile(member, interests);
    }

    public Map<String, String> updatePushNotification(PushNotificationRequestDto pushNotificationRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.setPushNotification(pushNotificationRequestDto.getPushNotification());

        return Collections.singletonMap("message", "푸시 알림 설정이 완료되었습니다.");
    }

    public Map<String, String> updateBirthDateDisclosure(BirthDateDisclosureRequestDto birthDateDisclosureRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.setBirthDateDisclosure(birthDateDisclosureRequestDto.getBirthDateDisclosure());

        return Collections.singletonMap("message", "생년월일 공개 여부 설정이 완료되었습니다.");
    }

    public Map<String, String> updateGenderDisclosure(genderDisclosureRequestDto genderDisclosure) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.setGenderDisclosure(genderDisclosure.getGenderDisclosure());

        return Collections.singletonMap("message", "성별 공개 여부 설정이 완료되었습니다.");
    }

    public Map<String, String> updateProfile(MemberProfileRequestDto memberProfileRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        member.updateMember(memberProfileRequestDto);

        return Collections.singletonMap("message", "프로필 수정이 완료되었습니다.");
    }

    public Map<String, String> updateDescription(DescriptionRequestDto descriptionRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        if (descriptionRequestDto.getDescription() != null) {
            member.setDescription(descriptionRequestDto.getDescription());
        }

        return Collections.singletonMap("message", "자기소개 수정이 완료되었습니다.");
    }

    public Map<String, String> updateKeywords(KeywordsRequestDto keywordsRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        if (keywordsRequestDto.getKeywords() != null) {
            memberKeywordRepository.deleteAllByMemberId(memberId);

            for (Keyword keyword : keywordsRequestDto.getKeywords()) {
                memberKeywordRepository.save(MemberKeyword.builder()
                        .member(member)
                        .keyword(keyword)
                        .build());
            }
        }

        return Collections.singletonMap("message", "관심사 수정이 완료되었습니다.");
    }
}
