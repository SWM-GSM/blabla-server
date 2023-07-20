package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
