package com.gsm.blabla.agora.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Accuse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccuseCategory category;

    private String description;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member reportee;

    @Builder
    public Accuse(AccuseCategory category, String description, Member reporter, Member reportee) {
        this.category = category;
        this.description = description;
        this.reporter = reporter;
        this.reportee = reportee;
    }
}
