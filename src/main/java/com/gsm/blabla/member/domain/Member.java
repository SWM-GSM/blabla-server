package com.gsm.blabla.member.domain;

import com.gsm.blabla.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType;

//    private String identifier; // 회원가입 시 사용한 전화번호 또는 이메일

    private String nickname; // 닉네임

    private String profileUrl;

    private LocalDate birthDate; // 생년월일

    private String gender; // 성별

    private String countryCode; // 국가 코드

    private String firstLang; // 자신 있는 언어

    private int firstLangLevel;

    private String secondLang; // 배우고 싶은 언어

    private int secondLangLevel;

    @OneToMany(mappedBy = "member")
    List<MemberKeyword> keywords;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER; // 역할

    // TODO: 변수명 바뀔 수 있음
    private double trustScore = 30; // 신뢰 점수

    private boolean pushNotification; // 푸시 알림 허용 여부

    @Builder
    public Member(SocialLoginType socialLoginType, String nickname, String profileUrl,
        LocalDate birthDate, String gender, String countryCode,
        String firstLang, int firstLangLevel,
        String secondLang, int secondLangLevel,
        List<MemberKeyword> interests, boolean pushNotification) {
        this.socialLoginType = socialLoginType;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.countryCode = countryCode;
        this.firstLang = firstLang;
        this.firstLangLevel = firstLangLevel;
        this.secondLang = secondLang;
        this.secondLangLevel = secondLangLevel;
        this.interests = interests;
        this.pushNotification = pushNotification;
    }
}
