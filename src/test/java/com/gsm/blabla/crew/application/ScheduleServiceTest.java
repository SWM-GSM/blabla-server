package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.CrewRepository;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class ScheduleServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;
    LocalDateTime meetingTime;
    String meetingTimeInString; // 현재 시각으로부터 3일 뒤
    CrewRequestDto crewRequestDto;
    ScheduleRequestDto scheduleRequestDto;

    @Autowired
    private ScheduleService scheduleService;

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

        meetingTime = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(16, 0, 0));
        meetingTimeInString =meetingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        scheduleRequestDto = createScheduleRequestDto(3);
    }

    @DisplayName("[POST] 유저가 크루 스페이스 스케줄을 성공적으로 생성한다.")
    @Test
    @WithCustomMockUser
    void create() {
        // given
        long scheduleIdBefore = scheduleRepository.count();
        long memberScheduleIdBefore = memberScheduleRepository.count();

        // when
        Long responseId = scheduleService.createSchedule(scheduleRequestDto).get("scheduleId");
        Optional<Schedule> schedule = scheduleRepository.findById(responseId);

        // then
        assertThat(responseId).isEqualTo(scheduleIdBefore + 1);
        assertThat(memberScheduleRepository.count()).isEqualTo(memberScheduleIdBefore + 1);
        assertThat(schedule).isPresent();
        assertThat(schedule.get().getId()).isEqualTo(responseId);
        assertThat(schedule.get().getTitle()).isEqualTo(scheduleRequestDto.getTitle());
        assertThat(schedule.get().getMeetingTime()).isEqualTo(
            LocalDateTime.parse(scheduleRequestDto.getMeetingTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @DisplayName("[POST] 크루 일정에 참여한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void joinSchedule() {
        // given
        ScheduleRequestDto scheduleRequestDtoWithId = ScheduleRequestDto.builder()
            .scheduleId(1L)
            .build();
        Long scheduleId = createPreparedSchedule(meetingTimeInString);
        Long memberInScheduleBefore = memberScheduleRepository.countBySchedule(scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다.")));

        // when
        String response = scheduleService.joinSchedule(scheduleRequestDtoWithId).get("message");
        Long memberInScheduleAfter = memberScheduleRepository.countBySchedule(scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다.")));

        // then
        assertThat(response).isEqualTo("일정 참여가 완료되었습니다.");
        assertThat(memberInScheduleAfter).isEqualTo(memberInScheduleBefore + 1);
    }

    @DisplayName("[GET] 모든 크루 스페이스 일정을 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getAll() {
        // given
        // 종료된 일정 - 2번 유저가 만든 일정
        Long schedule1 = scheduleService.createSchedule(ScheduleRequestDto.builder()
            .title("테스트 일정")
            .meetingTime("2023-01-01 00:00:00")
            .build()
        ).get("scheduleId");

        // 종료 이전 일정이며 참여 안한 일정 - 1번 유저가 만든 일정
        Long schedule2 = createPreparedSchedule(meetingTimeInString);

        // 종료 이전 일정이며 참여한 일정 - 2번 유저가 만든 일정
        Long schedule3 = scheduleService.createSchedule(scheduleRequestDto).get("scheduleId");

        // when
        List<ScheduleResponseDto> response = scheduleService.getAllSchedule().get("schedules");

        // then
        assertThat(response)
            .hasSize(3)
            .extracting("id", "meetingTime", "status")
            .containsExactly(
                tuple(schedule1, "2023-01-01 00:00:00", "ENDED"),
                tuple(schedule2, meetingTimeInString, "NOT_JOINED"),
                tuple(schedule3, meetingTime.plusDays(3).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "JOINED")
            );
    }

    @DisplayName("[GET] 다가오는 크루 일정을 조회한다.")
    @Test
    @WithCustomMockUser
    void getUpcomingSchedule() {
        // given
        scheduleService.createSchedule(createScheduleRequestDto(3)).get("scheduleId");
        scheduleService.createSchedule(createScheduleRequestDto(2)).get("scheduleId");
        Long schedule3 = scheduleService.createSchedule(createScheduleRequestDto(1)).get("scheduleId");

        // when
        ScheduleResponseDto response = scheduleService.getUpcomingSchedule();

        // then
        assertThat(response)
            .extracting("id", "title", "dDay", "meetingTime", "profiles")
            .contains(
                schedule3,
                scheduleRequestDto.getTitle(),
                4,
                meetingTime.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(member1.getProfileImage())
            );
    }

    @DisplayName("일정 참여 취소 시나리오")
    @TestFactory
    @WithCustomMockUser
    Collection<DynamicTest> cancelSchedule() {
        // given
        Long scheduleId = scheduleService.createSchedule(scheduleRequestDto).get("scheduleId");

        // when
        String cancelResult = scheduleService.cancelSchedule(ScheduleRequestDto.builder().scheduleId(scheduleId).build()).get("message");

        return List.of(
            DynamicTest.dynamicTest("[DELETE] 일정 참여를 취소한다.", () -> {
                // then
                assertThat(cancelResult).isEqualTo("일정 참여가 취소되었습니다.");
                assertThat(memberScheduleRepository.findByMemberAndSchedule(member1, scheduleRepository.findById(scheduleId)
                        .orElseThrow(() -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다."))
                    ).get().getStatus()
                ).isEqualTo("NOT_JOINED");
            }),
            DynamicTest.dynamicTest("[DELETE] 취소를 한 뒤, 다시 참여한다.", () -> {
                // when
                scheduleService.joinSchedule(ScheduleRequestDto.builder().scheduleId(scheduleId).build());

                // then
                assertThat(memberScheduleRepository.findByMemberAndSchedule(member1, scheduleRepository.findById(scheduleId)
                        .orElseThrow(() -> new GeneralException(Code.SCHEDULE_NOT_FOUND, "존재하지 않는 일정입니다."))
                    ).get().getStatus()
                ).isEqualTo("JOINED");
            }
            )
        );
    }

    private ScheduleRequestDto createScheduleRequestDto(int dayAfterMeetingTime) {
        return ScheduleRequestDto.builder()
            .title("테스트 일정")
            .meetingTime(meetingTime.plusDays(dayAfterMeetingTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
    }

    private Long createPreparedSchedule(String meetingTimeInString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime meetingTime = LocalDateTime.parse(meetingTimeInString, formatter);

        Schedule schedule = scheduleRepository.save(Schedule.builder()
            .title(scheduleRequestDto.getTitle())
            .meetingTime(meetingTime)
            .build()
        );

        memberScheduleRepository.save(MemberSchedule.builder()
            .member(member1)
            .schedule(schedule)
            .build()
        );

        return schedule.getId();
    }
}
