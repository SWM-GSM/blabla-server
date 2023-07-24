package com.gsm.blabla.dummy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {
    private Long id;
    private String title;
    private Integer dday;
    private String meetingTime;
    private List<String> profiles;
    private List<MemberDto> members;
}
