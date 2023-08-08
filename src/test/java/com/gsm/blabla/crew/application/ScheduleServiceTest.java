package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.MemberSchedule;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.dao.MemberScheduleRepository;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.crew.dto.ScheduleResponseDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class ScheduleServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;
    Long crewId;
    String meetingTime;
    CrewRequestDto crewRequestDto;
    ScheduleRequestDto scheduleRequestDto;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CrewService crewService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    MemberScheduleRepository memberScheduleRepository;

    @Autowired
    CrewRepository crewRepository;

    @BeforeEach
    void setUp() {
        member1 = createMember("cat");
        member2 = createMember("dog");

        crewRequestDto = CrewRequestDto.builder()
            .coverImage("test")
            .name("테스트")
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(8)
            .korLevel(1)
            .engLevel(1)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(true)
            .build();

        crewId = crewService.create(crewRequestDto).get("crewId");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(16, 0, 0));
        meetingTime = dateTime.format(formatter);

        scheduleRequestDto = createScheduleRequestDto(meetingTime);
    }

    @DisplayName("[POST] 크루 일정을 등록한다.")
    @Test
    @WithCustomMockUser
    void create() {
        // given
        long scheduleBefore = scheduleRepository.count();
        long memberScheduleBefore = memberScheduleRepository.count();

        // when
        Long response = scheduleService.create(crewId, scheduleRequestDto).get("scheduleId");

        // then
        assertThat(response).isEqualTo(scheduleBefore + 1);
        assertThat(memberScheduleRepository.count()).isEqualTo(memberScheduleBefore + 1);
    }

    @DisplayName("[POST] 크루 일정에 참여한다.")
    @Test
    @WithCustomMockUser
    void joinSchedule() {
        // given
        Long scheduleId = scheduleService.create(crewId, scheduleRequestDto).get("scheduleId");

        // when
        String response = scheduleService.joinSchedule(crewId, ScheduleRequestDto.builder().id(scheduleId).build())
            .get("message");

        // then
        assertThat(response).isEqualTo("일정 참여가 완료되었습니다.");
    }

    @DisplayName("[GET] 모든 크루 일정을 조회한다")
    @Test
    @WithCustomMockUser(id = "2")
    void getAll() {
        // given
        // 종료된 일정 - 2번 유저가 만든 일정
        scheduleService.create(crewId, createScheduleRequestDto("2023-01-01 00:00:00")).get("scheduleId");

        // 종료 이전 일정이며 참여 안한 일정 - 1번 유저가 만든 일정
        createPreparedSchedule(meetingTime);

        // 종료 이전 일정이며 참여한 일정 - 2번 유저가 만든 일정
        scheduleService.create(crewId, scheduleRequestDto).get("scheduleId");

        // when
        List<ScheduleResponseDto> response = scheduleService.getAll(crewId).get("schedules");

        // then
        assertThat(response)
            .hasSize(3)
            .extracting("status")
            .containsExactly("ENDED", "NOT_JOINED", "JOINED");
    }

    @DisplayName("[GET] 다가오는 크루 일정을 조회한다.")
    @Test
    @WithCustomMockUser
    void getUpcomingSchedule() {
        // given
        long memberScheduleBefore = memberScheduleRepository.count();
        Long scheduleId = scheduleService.create(crewId, scheduleRequestDto).get("scheduleId");

        // when
        ScheduleResponseDto response = scheduleService.getUpcomingSchedule(crewId);

        // then
        assertThat(response)
            .extracting("id", "title", "dDay", "meetingTime", "profiles")
            .contains(scheduleId, scheduleRequestDto.getTitle(), 3, scheduleRequestDto.getMeetingTime(), List.of(member1.getProfileImage())
            );
        assertThat(memberScheduleRepository.count()).isEqualTo(memberScheduleBefore + 1);
    }

    @DisplayName("[DELETE] 크루를 탈퇴할 경우 탈퇴 일자 이후 참여 일정에서 제외된다.")
    @Test
    @WithCustomMockUser
    void getScheduleAfterWithdrawal() {
        // given
        Long crewId = createCrew("테스트", true);
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));

        joinCrew(member2, crew);
        Long scheduleId = scheduleService.create(crewId, scheduleRequestDto).get("scheduleId");
        joinSchedule(member2, scheduleRepository.findById(scheduleId).orElseThrow(
            () -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다.")));

        List<String> beforeWithdrawal = scheduleService.getUpcomingSchedule(crewId).getProfiles();

        // when
        withdrawal(member2, crew);

        // then
        List<String> afterWithdrawal = scheduleService.getUpcomingSchedule(crewId).getProfiles();
        assertThat(beforeWithdrawal).isEqualTo(List.of(member1.getProfileImage(), member2.getProfileImage()));
        assertThat(afterWithdrawal).isEqualTo(List.of(member1.getProfileImage()));
    }

    @DisplayName("[DELETE] 크루를 탈퇴할 경우 탈퇴 일자 이전 참여 일정에서 유지된다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getScheduleBeforeWithdrawal() {
        // given

        // when

        // then
        assertThat(true).isFalse();
    }

    private ScheduleRequestDto createScheduleRequestDto(String meetingTime) {
        return ScheduleRequestDto.builder()
            .title("테스트 일정")
            .meetingTime(meetingTime)
            .build();
    }

    private Long createPreparedSchedule(String meetingTimeInString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime meetingTime = LocalDateTime.parse(meetingTimeInString, formatter);

        Schedule schedule = scheduleRepository.save(Schedule.builder()
            .title(scheduleRequestDto.getTitle())
            .meetingTime(meetingTime)
            .crew(crewRepository.findById(crewId).orElseThrow(
                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")))
            .build()
        );

        memberScheduleRepository.save(MemberSchedule.builder()
            .member(member1)
            .schedule(schedule)
            .build()
        );

        return schedule.getId();
    }

    private void joinSchedule(Member member, Schedule schedule) {
        memberScheduleRepository.save(
            MemberSchedule.builder()
                .member(member)
                .schedule(schedule)
                .build()
        );
    }

    private void withdrawal(Member member, Crew crew) {
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(member.getId(), crew.getId())
            .orElseThrow(() -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));
        crewMember.withdrawal();
    }
}
