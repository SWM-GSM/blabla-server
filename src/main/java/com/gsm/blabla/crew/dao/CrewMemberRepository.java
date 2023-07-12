package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    int countCrewMembersByCrewIdAndStatus(Long crewId, CrewMemberStatus status);
    CrewMember getByCrewIdAndMemberId(Long crewId, Long memberId);

}
