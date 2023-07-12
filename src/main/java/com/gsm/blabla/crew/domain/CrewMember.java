package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Enumerated(EnumType.STRING)
    private CrewMemberStatus status;

    @Enumerated(EnumType.STRING)
    private CrewMemberRole role;

    private LocalDateTime joinedAt;

    private LocalDateTime withdrawnAt;

    @Builder
    public CrewMember(Member member, Crew crew) {
        this.member = member;
        this.crew = crew;
        this.status = CrewMemberStatus.JOINED;
        this.role = CrewMemberRole.LEADER;
        this.joinedAt = LocalDateTime.now();
    }
}
