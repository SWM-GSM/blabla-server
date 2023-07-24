package com.gsm.blabla.crew.dto;

import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.MeetingCycle;
import java.util.List;
import lombok.Getter;

@Getter
public class CrewRequestDto {
    private String coverImage;
    private String name;
    private String description;
    private MeetingCycle meetingCycle;
    private List<Tag> tags;
    private int maxNum;
    private int korLevel;
    private int engLevel;
    private PreferMember preferMember;
    private String detail;
    private boolean autoApproval;

    public Crew toEntity() {
        return Crew.builder()
            .coverImage(coverImage)
            .name(name)
            .description(description)
            .meetingCycle(meetingCycle)
            .maxNum(maxNum)
            .korLevel(korLevel)
            .engLevel(engLevel)
            .preferMember(preferMember)
            .detail(detail)
            .autoApproval(autoApproval)
            .build();
    }
}
