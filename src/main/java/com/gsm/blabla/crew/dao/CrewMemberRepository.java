package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    int countCrewMembersByCrewIdAndStatus(Long crewId, CrewMemberStatus status);
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);
    Optional<CrewMember> getByCrewAndMemberAndStatus(Crew crew, Member member, CrewMemberStatus status);
}
