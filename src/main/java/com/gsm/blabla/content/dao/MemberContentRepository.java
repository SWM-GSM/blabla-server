package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.MemberContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberContentRepository extends JpaRepository<MemberContent, Long> {
    Optional<MemberContent> findByContentId(Long id);
}
