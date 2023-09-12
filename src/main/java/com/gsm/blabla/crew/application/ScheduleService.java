package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import com.gsm.blabla.crew.domain.MemberSchedule;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.crew.dto.ScheduleResponseDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;

    public Map<String, Long> createSchedule(ScheduleRequestDto scheduleRequestDto) {
        String meetingTimeInString = scheduleRequestDto.getMeetingTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime meetingTime = LocalDateTime.parse(meetingTimeInString, formatter);

        Schedule schedule = scheduleRepository.save(Schedule.builder()
            .title(scheduleRequestDto.getTitle())
            .meetingTime(meetingTime)
            .build()
        );

        Member member = memberRepository.findById(SecurityUtil.getMemberId()).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));

        memberScheduleRepository.save(MemberSchedule.builder()
                .member(member)
                .schedule(schedule)
            .build());

        return Collections.singletonMap("scheduleId", schedule.getId());
    }

    @Transactional(readOnly = true)
    public Map<String, List<ScheduleResponseDto>> getAllSchedule() {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));

        List<ScheduleResponseDto> schedules = scheduleRepository.findAllByOrderByMeetingTime()
            .stream()
            .map(schedule -> ScheduleResponseDto.scheduleListResponse(member, schedule, memberScheduleRepository))
            .toList();

        return Collections.singletonMap("schedules", schedules);
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getUpcomingSchedule() {
        Schedule schedule = scheduleRepository.findNearestSchedule();

        if (schedule == null) {
            return new ScheduleResponseDto();
        }

        return ScheduleResponseDto.scheduleResponse(schedule);
    }

    public Map<String, String> joinSchedule(ScheduleRequestDto scheduleRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));
        Schedule schedule = scheduleRepository.findById(scheduleRequestDto.getScheduleId()).orElseThrow(
                () -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다.")
        );

        Optional<MemberSchedule> memberSchedule = memberScheduleRepository.findByMemberAndSchedule(member, schedule);

        if (memberSchedule.isPresent()) {
            memberSchedule.get().joinAgain();
        } else {
            memberScheduleRepository.save(MemberSchedule.builder()
                .member(member)
                .schedule(schedule)
                .build()
            );
        }

        return Collections.singletonMap("message", "일정 참여가 완료되었습니다.");
    }

    public Map<String, String> cancelSchedule(ScheduleRequestDto scheduleRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));
        Schedule schedule = scheduleRepository.findById(scheduleRequestDto.getScheduleId()).orElseThrow(
                () -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다."));

        Optional<MemberSchedule> memberSchedule = memberScheduleRepository.findByMemberAndSchedule(member, schedule);

        if (memberSchedule.isEmpty()) {
            throw new GeneralException(Code.MEMBER_NOT_FOUND, "참여하지 않은 일정입니다.");
        }

        memberSchedule.get().cancel();

        return Collections.singletonMap("message", "일정 참여가 취소되었습니다.");
    }
}
