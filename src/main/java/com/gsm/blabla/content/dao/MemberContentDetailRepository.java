package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.MemberContentDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberContentDetailRepository extends JpaRepository<MemberContentDetail, Long> {

    Optional<MemberContentDetail> findByContentDetailIdAndMemberId(Long contentDetailId, Long memberId);
    List<MemberContentDetail> findAllByMemberId(Long memberId);

}
