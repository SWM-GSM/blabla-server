package com.gsm.blabla.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
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
    @Length(max = 12, message = "닉네임은 12자 이내여야 합니다.")
    private String nickname;
    private String profileImage;
    private Boolean pushNotification;
    private List<Long> ids;

    public Member toEntity() {
        return Member.builder()
            .socialLoginType(SocialLoginType.valueOf(socialLoginType))
            .nickname(nickname)
            .profileImage(profileImage)
            .pushNotification(pushNotification)
            .build();
    }
}
