package com.gsm.blabla.crew.domain;

import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.global.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Crew extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private MeetingCycle meetingCycle;
    private int maxNum;
    private int korLevel;
    private int engLevel;
    @Enumerated(EnumType.STRING)
    private PreferMember preferMember;
    private String detail;
    private boolean autoApproval;
    private String coverUrl;

    @OneToMany(mappedBy = "crew", fetch = FetchType.LAZY)
    List<CrewMember> crewMembers;

    @OneToMany(mappedBy = "crew", fetch = FetchType.LAZY)
    List<CrewTag> crewTags;

    @Builder
    public Crew(String name, String description, MeetingCycle meetingCycle, int maxNum, int korLevel,
        int engLevel, PreferMember preferMember, String detail, boolean autoApproval, String coverUrl) {
        this.name = name;
        this.description = description;
        this.meetingCycle = meetingCycle;
        this.maxNum = maxNum;
        this.korLevel = korLevel;
        this.engLevel = engLevel;
        this.preferMember = preferMember;
        this.detail = detail;
        this.autoApproval = autoApproval;
        this.coverUrl = coverUrl;
    }
}
