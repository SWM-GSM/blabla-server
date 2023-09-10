package com.gsm.blabla.auth.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class AppleAccount extends BaseTimeEntity {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    @Setter
    private String refreshToken;

    @OneToOne
    @JoinColumn
    private Member member;

    @Builder
    public AppleAccount(String id, String refreshToken, Member member) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.member = member;
    }
}
