package com.gsm.blabla.member.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import com.gsm.blabla.member.dto.MemberRequestDto;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE member SET is_withdrawal = true WHERE member_id = ?")
@Where(clause = "is_withdrawal = false")
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

    private Boolean isWithdrawal = false; // 탈퇴 여부

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

    public void updateMember(MemberRequestDto memberRequestDto) {
        Optional.ofNullable(memberRequestDto.getNickname()).ifPresent(this::setNickname);
        Optional.ofNullable(memberRequestDto.getProfileImage()).ifPresent(this::setProfileImage);
        Optional.ofNullable(memberRequestDto.getKorLevel()).ifPresent(this::setKorLevel);
        Optional.ofNullable(memberRequestDto.getEngLevel()).ifPresent(this::setEngLevel);
        Optional.ofNullable(memberRequestDto.getCountryCode()).ifPresent(this::setCountryCode);
        Optional.ofNullable(memberRequestDto.getDescription()).ifPresent(this::setDescription);
        Optional.ofNullable(memberRequestDto.getGender()).ifPresent(this::setGender);
    }
}