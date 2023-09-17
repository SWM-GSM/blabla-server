package com.gsm.blabla.agora.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class VoiceRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    private Boolean inVoiceRoom;

    @Builder
    public VoiceRoom(Member member, Boolean inVoiceRoom) {
        this.member = member;
        this.inVoiceRoom = inVoiceRoom;
    }

    public void updateInVoiceRoom(Boolean inVoiceRoom) {
        this.inVoiceRoom = inVoiceRoom;
    }
}
