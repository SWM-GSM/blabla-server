package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CrewAccuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CrewAccuseType type;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public CrewAccuse(CrewAccuseType type, String description, Crew crew, Member member) {
        this.type = type;
        this.description = description;
        this.crew = crew;
        this.member = member;
    }
}
