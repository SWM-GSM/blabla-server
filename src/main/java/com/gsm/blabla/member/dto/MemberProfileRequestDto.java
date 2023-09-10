package com.gsm.blabla.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemberProfileRequestDto {

    @Length(max = 12, message = "닉네임은 12자 이내여야 합니다.")
    private String nickname;

    private String profileImage;

    @Pattern(regexp = "^(ko|en)$", message = "언어는 ko 또는 en 중 하나여야 합니다.")
    private String learningLanguage;

}
