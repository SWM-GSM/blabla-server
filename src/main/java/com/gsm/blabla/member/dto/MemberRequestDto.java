package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import com.gsm.blabla.member.domain.nickname.Animal;
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
    private String learningLanguage;
    private Boolean pushNotification;
    private List<Long> ids;

    public Member toEntity(String nickname, String profileImage) {
        return Member.builder()
            .socialLoginType(SocialLoginType.valueOf(socialLoginType))
            .nickname(nickname)
            .profileImage(profileImage)
            .learningLanguage(learningLanguage)
            .pushNotification(pushNotification)
            .build();
    }
}
