package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findAllByLanguageOrderBySequence(String language);

}
