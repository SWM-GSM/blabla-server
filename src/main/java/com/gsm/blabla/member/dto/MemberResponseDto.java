package com.gsm.blabla.member.dto;

import com.gsm.blabla.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(member.getId());
    }
}
