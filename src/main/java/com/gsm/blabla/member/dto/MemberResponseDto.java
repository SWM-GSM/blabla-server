package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponseDto {
    private Long id;
    private String nickname;
    private String description;
    private String profileUrl;
    private LocalDate birthDate;
    private String gender;
    private String countryCode;
    private String firstLang;
    private int firstLangLevel;
    private String secondLang;
    private int secondLangLevel;
    private double trustScore;
    private boolean isLeader;

    public static MemberResponseDto crewProfileResponse(Long crewId, Member member,
        CrewMemberRepository crewMemberRepository) {

        return MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .description(member.getDescription())
            .profileUrl(member.getProfileUrl())
            .countryCode(member.getCountryCode())
            .firstLang(member.getFirstLang())
            .firstLangLevel(member.getFirstLangLevel())
            .secondLang(member.getSecondLang())
            .secondLangLevel(member.getSecondLangLevel())
            .trustScore(member.getTrustScore())
            .isLeader(crewMemberRepository.getByCrewIdAndMemberId(crewId, member.getId()).getRole() == CrewMemberRole.LEADER)
            .build();
    }
}
