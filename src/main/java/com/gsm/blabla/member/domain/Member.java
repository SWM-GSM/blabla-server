package com.gsm.blabla.member.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import com.gsm.blabla.member.dto.MemberProfileRequestDto;
import jakarta.persistence.*;

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
@SQLDelete(sql = "UPDATE member SET is_withdrawal = true WHERE id = ?")
@Where(clause = "is_withdrawal = false")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType; // 소셜 로그인 타입

    private String nickname; // 닉네임

    private String profileImage; // 프로필 이미지

    private String learningLanguage; // 배우고 싶은 언어

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER; // 역할

    private Boolean pushNotification = false; // 푸시 알림 허용 여부

    private Boolean isWithdrawal = false; // 탈퇴 여부


    @Builder
    public Member(SocialLoginType socialLoginType, String nickname, String profileImage,
        String learningLanguage) {
        this.socialLoginType = socialLoginType;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.learningLanguage = learningLanguage;
    }

    public void updateMember(MemberProfileRequestDto memberProfileRequestDto) {
        Optional.ofNullable(memberProfileRequestDto.getNickname()).ifPresent(this::setNickname);
        Optional.ofNullable(memberProfileRequestDto.getProfileImage()).ifPresent(this::setProfileImage);
        Optional.ofNullable(memberProfileRequestDto.getLearningLanguage()).ifPresent(this::setLearningLanguage);
    }
}
