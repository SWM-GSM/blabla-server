package com.gsm.blabla.member.dao;

import com.gsm.blabla.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
