package com.gsm.blabla.member.application;

import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
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

    public MemberResponseDto getMemberProfile(String language, Long memberId) {
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
                    } else if ("en".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getEnglishName());
                    }
                    return interest;
                })
                .toList();


        return MemberResponseDto.getMemberProfile(member, interests);
    }
}
