package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import com.gsm.blabla.crew.domain.Crew;
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
    private final CrewRepository crewRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Map<String, Long> create(Long crewId, ScheduleRequestDto scheduleRequestDto) {
        String meetingTimeInString = scheduleRequestDto.getMeetingTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime meetingTime = LocalDateTime.parse(meetingTimeInString, formatter);

        Schedule schedule = scheduleRepository.save(Schedule.builder()
            .title(scheduleRequestDto.getTitle())
            .meetingTime(meetingTime)
            .crew(crewRepository.findById(crewId).orElseThrow(
                    () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")))
            .build()
        );

        Member member = memberRepository.findById(SecurityUtil.getMemberId()).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));

        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, member.getId()).isEmpty()) {
            throw new GeneralException(Code.MEMBER_WITHOUT_PRIVILEGE, "크루에 가입되어 있지 않은 유저입니다.");
        }

        memberScheduleRepository.save(MemberSchedule.builder()
                .member(member)
                .schedule(schedule)
            .build());

        return Collections.singletonMap("scheduleId", schedule.getId());
    }

    @Transactional(readOnly = true)
    public Map<String, List<ScheduleResponseDto>> getAll(Long crewId) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));
        Crew crew = crewRepository.findById(crewId).orElseThrow(
                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        List<Schedule> schedules = scheduleRepository.findAllByCrewOrderByMeetingTime(crew);

        return Collections.singletonMap("schedules", schedules.stream()
            .map(schedule -> ScheduleResponseDto.scheduleListResponse(member, schedule,
                crewMemberRepository, memberScheduleRepository))
            .toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getUpcomingSchedule(Long crewId) {
        Schedule schedule = scheduleRepository.findNearestSchedule(crewId);

        if (schedule == null) {
            return new ScheduleResponseDto();
        }

        return ScheduleResponseDto.scheduleResponse(schedule, crewMemberRepository);
    }

    public Map<String, String> joinSchedule(Long crewId, ScheduleRequestDto scheduleRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));
        Crew crew = crewRepository.findById(crewId).orElseThrow(
                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        Schedule schedule = scheduleRepository.findByIdAndCrew(scheduleRequestDto.getId(), crew);

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

    public Map<String, String> cancelSchedule(Long crewId, ScheduleRequestDto scheduleRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));
        Crew crew = crewRepository.findById(crewId).orElseThrow(
                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        Schedule schedule = scheduleRepository.findByIdAndCrew(scheduleRequestDto.getId(), crew);

        Optional<MemberSchedule> memberSchedule = memberScheduleRepository.findByMemberAndSchedule(member, schedule);

        if (memberSchedule.isEmpty()) {
            throw new GeneralException(Code.MEMBER_NOT_FOUND, "참여하지 않은 일정입니다.");
        }

        memberSchedule.get().cancel();

        return Collections.singletonMap("message", "일정 참여가 취소되었습니다.");
    }
}
