package com.gsm.blabla.practice.dao;

import com.gsm.blabla.practice.domain.Content;
import com.gsm.blabla.practice.domain.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findAllByContentCategory(ContentCategory contentCategory);
}
