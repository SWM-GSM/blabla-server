package com.gsm.blabla.crew.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String meetingTime;
    private int dDay;
    private List<String> profiles;

    public static ScheduleResponseDto of(Long id, String title, String meetingTime, int dDay, List<String> profiles) {
        return ScheduleResponseDto.builder()
            .id(id)
            .title(title)
            .meetingTime(meetingTime)
            .dDay(dDay)
            .profiles(profiles)
            .build();
    }
}
