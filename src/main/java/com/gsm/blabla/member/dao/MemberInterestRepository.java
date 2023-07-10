package com.gsm.blabla.member.dao;

import com.gsm.blabla.member.domain.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInterestRepository extends JpaRepository<MemberKeyword, Long> {

}
