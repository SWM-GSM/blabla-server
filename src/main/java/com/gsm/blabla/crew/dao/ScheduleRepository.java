package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from Schedule s "
        + "where month(s.meetingTime) = :month and day(s.meetingTime) = :day and s.crew = :crew")
    List<Schedule> findSchedulesByMeetingTimeAndCrew(@Param("month") int month, @Param("day") int day, @Param("crew") Crew crew);
}
