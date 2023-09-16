package com.gsm.blabla.crew.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.global.RepositoryTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class ScheduleRepositoryTest extends RepositoryTestSupport {

    @DisplayName("일정을 오름차순으로 조회한다.")
    @Test
    void findAllByOrderByMeetingTime() {
        // given
        Schedule schedule1 = createSchedule(LocalDateTime.of(2023, 1, 1, 0, 0), "test1");
        Schedule schedule2 = createSchedule(LocalDateTime.of(2023, 1, 2, 0, 0), "test2");
        Schedule schedule3 = createSchedule(LocalDateTime.of(2023, 1, 3, 0, 0), "test3");
        scheduleRepository.saveAll(List.of(schedule1, schedule2, schedule3));

        // when
        List<Schedule> result = scheduleRepository.findAllByOrderByMeetingTime();

        // then
        assertThat(result).hasSize(3)
            .extracting("meetingTime", "title")
            .containsExactly(
                tuple(LocalDateTime.of(2023, 1, 1, 0, 0), "test1"),
                tuple(LocalDateTime.of(2023, 1, 2, 0, 0), "test2"),
                tuple(LocalDateTime.of(2023, 1, 3, 0, 0), "test3")
            );
    }

    @DisplayName("가장 가까운 일정을 조회한다.")
    @Test
    void findNearsestScedule() {
        // given
        Schedule schedule1 = createSchedule(LocalDateTime.of(2023, 1, 1, 0, 0), "test1");
        Schedule schedule2 = createSchedule(LocalDateTime.now().plusDays(5), "test2");
        Schedule schedule3 = createSchedule(LocalDateTime.now().plusDays(1), "test3");
        Schedule schedule4 = createSchedule(LocalDateTime.now().plusDays(3), "test4");
        scheduleRepository.saveAll(List.of(schedule1, schedule2, schedule3, schedule4));

        // when
        Schedule result = scheduleRepository.findNearestSchedule();

        // then
        assertThat(result.getTitle()).isEqualTo("test3");
    }

    private Schedule createSchedule(LocalDateTime meetingTime, String title) {
        return Schedule.builder()
            .meetingTime(meetingTime)
            .title(title)
            .build();
    }
}
