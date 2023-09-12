package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Schedule;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s "
        + "where s.meetingTime >= NOW() "
        + "order by (s.meetingTime - NOW()) "
        + "limit 1")
    Schedule findNearestSchedule();

    List<Schedule> findAllByOrderByMeetingTime();

    @NotNull
    Optional<Schedule> findById(@NotNull Long id);
}
