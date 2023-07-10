package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.global.common.enums.Keyword;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberRequestDto {
    private String socialLoginType;
    private String nickname;
    private String birthDate; // yyyy-MM-dd
    private String gender;
    private String countryCode;
    private String firstLang;

    private int firstLangLevel;
    private String secondLang;
    private int secondLangLevel;
    private List<Keyword> keywords;
    private boolean pushNotification;

    public Member toEntity(String profileUrl) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return Member.builder()
            .socialLoginType(SocialLoginType.valueOf(socialLoginType))
            .nickname(nickname)
            .profileUrl(profileUrl)
            .birthDate(LocalDate.parse(birthDate, formatter))
            .gender(gender)
            .countryCode(countryCode)
            .firstLang(firstLang)
            .firstLangLevel(firstLangLevel)
            .secondLang(secondLang)
            .secondLangLevel(secondLangLevel)
            .pushNotification(pushNotification)
            .build();
    }
}
