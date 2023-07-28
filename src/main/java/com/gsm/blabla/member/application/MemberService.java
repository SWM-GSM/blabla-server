package com.gsm.blabla.member.application;

import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;

import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.dto.MemberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberKeywordRepository memberKeywordRepository;

    public Map<String, Boolean> isNicknameDuplicated(String nickname) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isDuplicated", memberRepository.findByNickname(nickname).isPresent());

        return result;
    }

    public Map<String, String> updateProfile(MemberRequestDto memberRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        if (memberRequestDto.getKeywords() != null) {
            memberKeywordRepository.deleteAllByMemberId(memberId);

            for (Keyword keyword : memberRequestDto.getKeywords()) {
                memberKeywordRepository.save(MemberKeyword.builder()
                        .member(member)
                        .keyword(keyword)
                        .build());
            }
        }

        member.updateMember(memberRequestDto);

        return Collections.singletonMap("message", "프로필 수정이 완료되었습니다.");
    }
}
