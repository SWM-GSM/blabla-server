package com.gsm.blabla.crew.dto;

import com.gsm.blabla.crew.domain.Schedule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static ScheduleResponseDto of(Schedule schedule) {
        return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .title(schedule.getTitle())
            .meetingTime(schedule.getMeetingTime().format(
                DateTimeFormatter.ofPattern("M월 dd일 E요일 a h:mm")
            ))
            .dDay(schedule.getMeetingTime().getDayOfYear() - LocalDateTime.now().getDayOfYear())
            .profiles(schedule.getMemberSchedules().stream().map(
                        memberSchedule -> memberSchedule.getMember().getProfileImage()
                    ).toList())
            .build();
    }
}
