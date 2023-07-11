package com.gsm.blabla.crew.dto;

import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.crew.domain.Crew;
import java.util.List;
import lombok.Getter;

@Getter
public class CrewRequestDto {
    private String name;
    private String description;
    // TODO: 희망 모임 주기 추가하기
    private List<Tag> tags;
    private int maxNum;
    private int korLevel;
    private int engLevel;
    private PreferMember preferMember;
    private String detail;
    private boolean autoApproval;

    public Crew toEntity(String coverUrl) {
        return Crew.builder()
            .name(name)
            .description(description)
            .maxNum(maxNum)
            .korLevel(korLevel)
            .engLevel(engLevel)
            .preferMember(preferMember)
            .detail(detail)
            .autoApproval(autoApproval)
            .coverUrl(coverUrl)
            .build();
    }
}
