package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import com.gsm.blabla.crew.domain.MemberSchedule;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.crew.dto.CrewScheduleRequestDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CrewRepository crewRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Map<String, Long> create(Long crewId, CrewScheduleRequestDto crewScheduleRequestDto) {
        String meetingTimeInString = crewScheduleRequestDto.getMeetingTime();
        LocalDateTime meetingTime = LocalDateTime.parse(meetingTimeInString);

        Schedule schedule = scheduleRepository.save(Schedule.builder()
            .title(crewScheduleRequestDto.getTitle())
            .meetingTime(meetingTime)
            .crew(crewRepository.findById(crewId).orElseThrow(
                    () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")))
            .build()
        );

        Member member = memberRepository.findById(SecurityUtil.getMemberId()).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다."));

        if (crewMemberRepository.getByCrewIdAndMemberId(crewId, member.getId()) == null) {
            throw new GeneralException(Code.MEMBER_WITHOUT_PRIVILEGE, "크루에 가입되어 있지 않은 유저입니다.");
        }

        memberScheduleRepository.save(MemberSchedule.builder()
                .member(member)
                .schedule(schedule)
            .build());

        return Collections.singletonMap("scheduleId", schedule.getId());
    }
}
