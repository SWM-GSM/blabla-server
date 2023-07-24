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
import org.hibernate.annotations.BatchSize;

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
    private Integer korLevel;
    private Integer engLevel;
    @Enumerated(EnumType.STRING)
    private PreferMember preferMember;
    private String detail;
    private Boolean autoApproval;
    private String coverImage;

    @OneToMany(mappedBy = "crew")
    List<CrewMember> crewMembers;

    @OneToMany(mappedBy = "crew")
    List<CrewTag> crewTags;

    @Builder
    public Crew(String name, String description, MeetingCycle meetingCycle, int maxNum, int korLevel,
        int engLevel, PreferMember preferMember, String detail, Boolean autoApproval, String coverImage) {
        this.name = name;
        this.description = description;
        this.meetingCycle = meetingCycle;
        this.maxNum = maxNum;
        this.korLevel = korLevel;
        this.engLevel = engLevel;
        this.preferMember = preferMember;
        this.detail = detail;
        this.autoApproval = autoApproval;
        this.coverImage = coverImage;
    }
}
