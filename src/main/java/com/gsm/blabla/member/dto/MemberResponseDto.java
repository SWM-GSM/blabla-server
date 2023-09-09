package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.member.domain.Member;

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
    private String profileImage;
    private String comment;

    public static MemberResponseDto crewReportResponse(Member member) {
        return MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .profileImage(member.getProfileImage())
            .build();
    }

    public static MemberResponseDto feedbackResponse(Member member, VoiceFile voiceFile) {
        if (voiceFile.getFeedback() == null) return null;

        return MemberResponseDto.builder()
            .nickname(member.getNickname())
            .profileImage(member.getProfileImage())
            .comment(voiceFile.getFeedback())
            .build();
    }
}
