package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileResponseDto {
    private String nickname;
    private String description;
    private String profileImage;
    private LocalDate birthDate;
    private String gender;
    private String countryCode;
    private Integer korLevel;
    private Integer engLevel;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLeader;
    private Long signedUpAfter;
    List<Map<String, String>> keywords;

    public static MemberProfileResponseDto getCrewMemberProfile(Member member, CrewMember crewMember, List<Map<String, String>> memberInterestList) {
        return MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .description(member.getDescription())
                .profileImage(member.getProfileImage())
                .countryCode(member.getCountryCode())
                .korLevel(member.getKorLevel())
                .engLevel(member.getEngLevel())
                .birthDate(member.getBirthDateDisclosure() ? member.getBirthDate() : null)
                .gender(member.getGenderDisclosure() ? member.getGender() : null)
                .signedUpAfter((long) member.getCreatedAt().toLocalDate().until(LocalDateTime.now().toLocalDate()).getDays() + 1)
                .isLeader(crewMember.getRole() == CrewMemberRole.LEADER)
                .keywords(memberInterestList)
                .build();
    }

    public static MemberProfileResponseDto getMemberProfile(Member member, List<Map<String, String>> memberInterestList) {
        return MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .description(member.getDescription())
                .profileImage(member.getProfileImage())
                .countryCode(member.getCountryCode())
                .korLevel(member.getKorLevel())
                .engLevel(member.getEngLevel())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .signedUpAfter((long) member.getCreatedAt().toLocalDate().until(LocalDateTime.now().toLocalDate()).getDays() + 1)
                .isLeader(null)
                .keywords(memberInterestList)
                .build();
    }
}
