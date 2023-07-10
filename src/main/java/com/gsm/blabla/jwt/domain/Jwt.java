package com.gsm.blabla.jwt.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Jwt extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Jwt(Member member, String refreshToken) {
        this.member = member;
        this.refreshToken = refreshToken;
    }


}
