package com.gsm.blabla.member.dto;

import com.gsm.blabla.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileResponseDto {
    private String nickname;
    private String profileImage;

    public static MemberProfileResponseDto getCrewMemberProfile(Member member) {
        return MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .build();
    }

    public static MemberProfileResponseDto getMemberProfile(Member member) {
        return MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }
}
