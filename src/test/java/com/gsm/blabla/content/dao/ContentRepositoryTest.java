package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.global.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ContentRepositoryTest extends RepositoryTestSupport {

    @DisplayName("영어 컨텐츠 리스트를 순서대로 조회한다.")
    @Test
    void findAllByLanguageOrderBySequence() {
        // given
        Content content1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en", 300L);
        Content content2 = createContent("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en", 200L);
        Content content3 = createContent("오징어 게임", "드라마 오징어 게임을 통해 줄임말 표현을 배워봅시다.", "ko", 100L);
        Content content4 = createContent("왕좌의 게임", "왕좌의 게임을 통해 전쟁 표현을 배워봅시다.", "en", 100L);

        // when
        List<Content> contents = contentRepository.findAllByLanguageOrderBySequence("en");

        // then
        assertThat(contents).hasSize(3)
                .extracting("title", "description", "language")
                .containsExactly(
                    tuple("왕좌의 게임", "왕좌의 게임을 통해 전쟁 표현을 배워봅시다.", "en"),
                    tuple("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en"),
                    tuple("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en")
                );
    }

}
