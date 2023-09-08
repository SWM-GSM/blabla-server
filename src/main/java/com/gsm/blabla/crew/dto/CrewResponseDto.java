package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
