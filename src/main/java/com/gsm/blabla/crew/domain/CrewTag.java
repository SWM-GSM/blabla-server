package com.gsm.blabla.crew.domain;

import com.gsm.blabla.common.enums.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class CrewTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Builder
    public CrewTag(Crew crew, Tag tag) {
        this.crew = crew;
        this.tag = tag;
    }
}
