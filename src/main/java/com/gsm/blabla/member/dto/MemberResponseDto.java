package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.crew.dao.ApplyMessageRepository;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.domain.Role;
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
    private String application;
    private String profileImage;
    private LocalDate birthDate;
    private String gender;
    private String countryCode;
    private Integer korLevel;
    private Integer engLevel;
    private Boolean isLeader;

    public static MemberResponseDto crewProfileResponse(Long crewId, Member member,
        CrewMemberRepository crewMemberRepository) {

        return MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .description(member.getDescription())
            .profileImage(member.getProfileImage())
            .countryCode(member.getCountryCode())
            .korLevel(member.getKorLevel())
            .engLevel(member.getEngLevel())
            .isLeader(crewMemberRepository.findByCrewIdAndMemberId(crewId, member.getId())
                .orElseThrow(() -> new GeneralException(Code.MEMBER_NOT_JOINED, "해당 멤버는 해당 크루의 멤버가 아닙니다."))
                .getRole() == CrewMemberRole.LEADER)
            .build();
    }

    public static MemberResponseDto waitingListResponse(Long crewId, Member member, ApplyMessageRepository applyMessageRepository) {
        return MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .description(member.getDescription())
            .application(applyMessageRepository.getByCrewIdAndMemberId(crewId, member.getId())
                .orElseThrow(() -> new GeneralException(Code.APPLY_NOT_FOUND, "가입 신청 내역을 찾을 수 없습니다."))
                .getMessage())
            .profileImage(member.getProfileImage())
            .countryCode(member.getCountryCode())
            .korLevel(member.getKorLevel())
            .engLevel(member.getEngLevel())
            .build();
    }
}
