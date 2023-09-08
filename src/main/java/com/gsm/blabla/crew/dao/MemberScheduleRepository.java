package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.MemberSchedule;
import com.gsm.blabla.crew.domain.Schedule;
import com.gsm.blabla.member.domain.Member;
import java.util.Optional;
import java.util.OptionalInt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

    Optional<MemberSchedule> findByMemberAndSchedule(Member member, Schedule schedule);
}
