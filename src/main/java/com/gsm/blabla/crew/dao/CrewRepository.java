package com.gsm.blabla.crew.dao;

import com.gsm.blabla.crew.domain.Crew;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    @Query(
        "select c from Crew c "
        + "where c.korLevel <= :korLevel "
        + "and c.engLevel <= :engLevel "
        + "and c.id not in (select cm.crew.id from CrewMember cm where cm.member.id = :memberId)"
    )
    List<Crew> findCrewsThatCanBeJoined(Long memberId, int korLevel, int engLevel);
}
