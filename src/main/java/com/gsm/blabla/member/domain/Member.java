package com.gsm.blabla.member.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType;

//    private String identifier; // 회원가입 시 사용한 전화번호 또는 이메일

    private String nickname; // 닉네임

    private String profileUrl;

    private LocalDate birthDate; // 생년월일

    private String gender; // 성별

    private String countryCode; // 국가 코드

    private int korLevel;

    private int engLevel;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER; // 역할

    // TODO: 변수명 바뀔 수 있음
    private double trustScore = 30; // 신뢰 점수

    private boolean pushNotification; // 푸시 알림 허용 여부

    private String description = ""; // 자기소개

    @Builder
    public Member(SocialLoginType socialLoginType, String nickname, String profileUrl,
        LocalDate birthDate, String gender, String countryCode,
        int korLevel, int engLevel, boolean pushNotification) {
        this.socialLoginType = socialLoginType;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.countryCode = countryCode;
        this.korLevel = korLevel;
        this.engLevel = engLevel;
        this.pushNotification = pushNotification;
    }
}
