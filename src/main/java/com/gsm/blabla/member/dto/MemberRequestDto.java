package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.domain.SocialLoginType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberRequestDto {
    private String socialLoginType;
    private String profileImage;
    @Length(max = 12, message = "닉네임은 12자 이내여야 합니다.")
    private String nickname;
    private String birthDate; // yyyy-MM-dd
    private String gender;
    private String countryCode;
    @Min(value = 1, message = "레벨은 1에서 5 사이여야 합니다.")
    @Max(value = 5, message = "레벨은 1에서 5 사이여야 합니다.")
    private int korLevel;
    @Min(value = 1, message = "레벨은 1에서 5 사이여야 합니다.")
    @Max(value = 5, message = "레벨은 1에서 5 사이여야 합니다.")
    private int engLevel;
    @Size(max = 10, message = "관심사는 10개까지 선택 가능합니다.")
    private List<Keyword> keywords;
    private String description;
    private boolean pushNotification;

    public Member toEntity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return Member.builder()
            .socialLoginType(SocialLoginType.valueOf(socialLoginType))
            .nickname(nickname)
            .profileImage(profileImage)
            .birthDate(LocalDate.parse(birthDate, formatter))
            .gender(gender)
            .countryCode(countryCode)
            .korLevel(korLevel)
            .engLevel(engLevel)
            .pushNotification(pushNotification)
            .build();
    }
}
