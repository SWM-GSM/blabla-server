package com.gsm.blabla.member.dao;

import com.gsm.blabla.member.domain.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {
    List<MemberKeyword> findAllByMemberId(Long memberId);
}
