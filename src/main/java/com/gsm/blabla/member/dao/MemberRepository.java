package com.gsm.blabla.member.dao;

import com.gsm.blabla.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);

    @Query("select max(m.id) from Member m")
    Long findLastId();
}
