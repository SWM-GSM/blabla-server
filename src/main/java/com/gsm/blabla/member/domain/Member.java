package com.gsm.blabla.member.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType; // 소셜 로그인 타입

    private String nickname; // 닉네임

    private String profileImage; // 프로필 이미지

    private LocalDate birthDate; // 생년월일

    private String gender; // 성별

    private String countryCode; // 국가 코드

    private int korLevel; // 한국어 레벨

    private int engLevel; // 영어 레벨

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER; // 역할

    private boolean pushNotification; // 푸시 알림 허용 여부

    private String description = ""; // 자기소개

    @Builder
    public Member(SocialLoginType socialLoginType, String nickname, String profileImage,
        LocalDate birthDate, String gender, String countryCode,
        int korLevel, int engLevel, boolean pushNotification) {
        this.socialLoginType = socialLoginType;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.birthDate = birthDate;
        this.gender = gender;
        this.countryCode = countryCode;
        this.korLevel = korLevel;
        this.engLevel = engLevel;
        this.pushNotification = pushNotification;
    }
}
