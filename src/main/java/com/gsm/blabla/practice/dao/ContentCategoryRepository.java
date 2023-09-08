package com.gsm.blabla.practice.dao;

import com.gsm.blabla.practice.domain.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

    List<ContentCategory> findAllByLanguage(String language);
}
