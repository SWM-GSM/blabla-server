package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewResponseDto {
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
    private String  createdAt;
    private String status;
    private List<MemberResponseDto> members;
    private List<String> tags;
}
