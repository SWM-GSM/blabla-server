package com.gsm.blabla.practice.dao;

import com.gsm.blabla.practice.domain.MemberContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberContentRepository extends JpaRepository<MemberContent, Long> {
    Optional<MemberContent> findByContentIdAndMemberId(Long contentId, Long memberId);
    List<MemberContent> findAllByMemberId(Long memberId);
}
