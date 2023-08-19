package com.gsm.blabla.jwt.domain;

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
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString(of = "id")
public class GoogleAccount extends BaseTimeEntity {
    @Id
    @Column
    private String id;

    @OneToOne
    @JoinColumn
    private Member member;

    @Builder
    public GoogleAccount(String id, Member member) {
        this.id = id;
        this.member = member;
    }
}
