package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String meetingTime;
    private Integer dDay;
    private List<String> profiles;
    private String status; // ENDED, NOT_JOINED, JOINED

    public static ScheduleResponseDto scheduleResponse(Schedule schedule, CrewMemberRepository crewMemberRepository) {
        List<String> profiles = getProfiles(schedule, crewMemberRepository);

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

    public static ScheduleResponseDto scheduleListResponse(Member member, Schedule schedule,
        CrewMemberRepository crewMemberRepository, MemberScheduleRepository memberScheduleRepository) {
        List<String> profiles = getProfiles(schedule, crewMemberRepository);
        String status = getStatus(member, schedule, memberScheduleRepository);

        return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .title(schedule.getTitle())
            .meetingTime(schedule.getMeetingTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ))
            .dDay(schedule.getMeetingTime().getDayOfYear() - LocalDateTime.now().getDayOfYear())
            .profiles(profiles)
            .status(status)
            .build();
    }

    private static List<String> getProfiles(Schedule schedule, CrewMemberRepository crewMemberRepository) {
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

        return profiles;
    }

    private static String getStatus(Member member, Schedule schedule, MemberScheduleRepository memberScheduleRepository) {
        String status = "";

        if (schedule.getMeetingTime().isBefore(LocalDateTime.now())) {
            status = "ENDED";
        } else {
            boolean isJoined = memberScheduleRepository.findByMemberAndSchedule(member, schedule).isPresent();
            status = isJoined ? "JOINED" : "NOT_JOINED";
        }

        return status;
    }
}
