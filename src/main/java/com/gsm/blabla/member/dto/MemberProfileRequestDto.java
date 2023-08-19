package com.gsm.blabla.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
public class MemberProfileRequestDto {
    @Length(max = 12, message = "닉네임은 12자 이내여야 합니다.")
    private String nickname;
    private String profileImage;
    @Min(value = 1, message = "레벨은 1에서 5 사이여야 합니다.")
    @Max(value = 5, message = "레벨은 1에서 5 사이여야 합니다.")
    private Integer korLevel;
    @Min(value = 1, message = "레벨은 1에서 5 사이여야 합니다.")
    @Max(value = 5, message = "레벨은 1에서 5 사이여야 합니다.")
    private Integer engLevel;
    private String countryCode;
    private LocalDate birthDate; // yyyy-MM-dd
    private String gender;
}
