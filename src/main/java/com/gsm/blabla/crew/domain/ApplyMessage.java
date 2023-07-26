package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ApplyMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    private ApplyMessageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ApplyMessage(String message, ApplyMessageStatus status, Crew crew, Member member) {
        this.message = message;
        this.status = ApplyMessageStatus.WAITING;
        this.crew = crew;
        this.member = member;
    }

    public void acceptOrReject(String status) {
        if (status.equals("accept")) {
            this.status = ApplyMessageStatus.ACCEPT;
        } else if (status.equals("reject")) {
            this.status = ApplyMessageStatus.REJECT;
        }
    }
}
