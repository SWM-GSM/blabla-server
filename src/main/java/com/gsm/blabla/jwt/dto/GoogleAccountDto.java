package com.gsm.blabla.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gsm.blabla.member.dto.MemberRequestDto;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class GoogleAccountDto {
    private String id;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;

    // TODO: 나중에 프로필 사진 추가하기
    public MemberRequestDto toMemberRequestDto() {
        return MemberRequestDto.builder()
            .nickname(name)
            .countryCode(locale)
            .build();
    }
}
