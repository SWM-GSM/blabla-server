package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentDetailRepository extends JpaRepository<ContentDetail, Long> {

    List<ContentDetail> findAllByContent(Content content);

}
