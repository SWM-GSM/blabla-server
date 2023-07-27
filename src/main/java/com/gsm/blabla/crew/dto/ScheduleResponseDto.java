package com.gsm.blabla.crew.dto;

import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.Schedule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String meetingTime;
    private Integer dDay;
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

    public static ScheduleResponseDto of(Schedule schedule, CrewMemberRepository crewMemberRepository) {
        List<String> profiles;
        if (schedule.getMeetingTime().isBefore(LocalDateTime.now())) {
            profiles = schedule.getMemberSchedules().stream().map(
                memberSchedule -> memberSchedule.getMember().getProfileImage()
            ).toList();
        } else {
            profiles = schedule.getMemberSchedules().stream().map(
                memberSchedule -> {
                    boolean isJoined = crewMemberRepository.getByCrewAndMemberAndStatus(schedule.getCrew(), memberSchedule.getMember(), CrewMemberStatus.JOINED).isPresent();
                    return isJoined ? memberSchedule.getMember().getProfileImage() : null;
                }
            ).filter(Objects::nonNull).toList();
        }

        return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .title(schedule.getTitle())
            .meetingTime(schedule.getMeetingTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ))
            .dDay(schedule.getMeetingTime().getDayOfYear() - LocalDateTime.now().getDayOfYear())
            .profiles(profiles)
            .build();
    }
}
