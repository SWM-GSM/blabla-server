package com.gsm.blabla.member.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemberProfileRequestDto {
    @Length(max = 12, message = "닉네임은 12자 이내여야 합니다.")
    private String nickname;
    private String profileImage;
    private String learningLanguage;
}
