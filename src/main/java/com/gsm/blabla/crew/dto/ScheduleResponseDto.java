package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.member.domain.Member;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String meetingTime;
    private Integer dDay;
    private List<String> profiles;
    private List<MemberResponseDto> members;
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
        List<MemberResponseDto> members = getMembers(schedule, crewMemberRepository);
        String status = getStatus(member, schedule, memberScheduleRepository);

        return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .title(schedule.getTitle())
            .meetingTime(schedule.getMeetingTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ))
            .dDay(schedule.getMeetingTime().getDayOfYear() - LocalDateTime.now().getDayOfYear())
            .members(members)
            .status(status)
            .build();
    }

    private static List<String> getProfiles(Schedule schedule, CrewMemberRepository crewMemberRepository) {
        List<String> profiles;

        if (schedule.getMeetingTime().isBefore(LocalDateTime.now())) {
            profiles = schedule.getMemberSchedules().stream()
                .filter(memberSchedule -> memberSchedule.getStatus().equals("JOINED"))
                .map(
                memberSchedule -> memberSchedule.getMember().getProfileImage()
            ).toList();
        } else {
            profiles = schedule.getMemberSchedules().stream()
                .filter(memberSchedule -> memberSchedule.getStatus().equals("JOINED"))
                .map(
                memberSchedule -> {
                    boolean isJoined = crewMemberRepository.getByCrewAndMemberAndStatus(schedule.getCrew(), memberSchedule.getMember(), CrewMemberStatus.JOINED).isPresent();
                    return isJoined ? memberSchedule.getMember().getProfileImage() : null;
                }
            ).filter(Objects::nonNull).toList();
        }

        return profiles;
    }

    private static List<MemberResponseDto> getMembers(Schedule schedule, CrewMemberRepository crewMemberRepository) {
        // TODO: crewReportResponse 가져다 써도 의미가 파악이 되는지?
        List<MemberResponseDto> members;
        if (schedule.getMeetingTime().isBefore(LocalDateTime.now())) {
            members = schedule.getMemberSchedules().stream()
                .filter(memberSchedule -> memberSchedule.getStatus().equals("JOINED"))
                .map(
                    memberSchedule -> MemberResponseDto.crewReportResponse(memberSchedule.getMember())
                ).toList();
        } else {
            members = schedule.getMemberSchedules().stream()
                .filter(memberSchedule -> memberSchedule.getStatus().equals("JOINED"))
                .map(
                    memberSchedule -> {
                        boolean isJoined = crewMemberRepository.getByCrewAndMemberAndStatus(schedule.getCrew(), memberSchedule.getMember(), CrewMemberStatus.JOINED).isPresent();
                        return isJoined ? MemberResponseDto.crewReportResponse(memberSchedule.getMember()) : null;
                    }
                ).filter(Objects::nonNull).toList();
        }

        return members;
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
