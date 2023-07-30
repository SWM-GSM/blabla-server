package com.gsm.blabla.practice.dao;

import com.gsm.blabla.practice.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByLevel(int level);
    List<Content> findAllByLanguage(String language);
}
