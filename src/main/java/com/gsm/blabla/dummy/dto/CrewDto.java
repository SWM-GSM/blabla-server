package com.gsm.blabla.dummy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewDto {
    private Long id;
    private String name;
    private String description;
    private String meetingCycle;
    private Integer maxNum;
    private Integer currentNum;
    private Integer korLevel;
    private String korLevelText;
    private Integer engLevel;
    private String engLevelText;
    private String preferMember;
    private String detail;
    private Boolean autoApproval;
    private String coverImage;
    private String createdAt;
    private CrewMemberStatus status;
    private List<MemberDto> members;
    private List<String> tags;
}
