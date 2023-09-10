package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @DisplayName("언어를 통해 컨텐츠 리스트를 조회한다.")
    @Test
    void findAllByLanguage() {
        // given
        Content content1 = Content.builder()
                .title("인턴")
                .subtitle("비즈니스 표현 배우기")
                .description("영화 인턴을 통해 비즈니스 표현을 배워봅시다.")
                .language("en")
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .build();
        Content content2 = Content.builder()
                .title("셜록")
                .subtitle("일상 표현 배우기")
                .description("셜록을 통해 영국 억양과 일상 표현을 배워봅시다.")
                .language("en")
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .build();
        Content content3 = Content.builder()
                .title("오징어 게임")
                .subtitle("한국 줄임말 배우기")
                .description("드라마 오징어 게임을 통해 줄임말 표현을 배워봅시다.")
                .language("ko")
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .build();
        Content content4 = Content.builder()
                .title("왕좌의 게임")
                .subtitle("전쟁 표현 배우기")
                .description("왕좌의 게임을 통해 전쟁 표현을 배워봅시다.")
                .language("en")
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .build();
        contentRepository.saveAll(List.of(content1, content2, content3, content4));

        // when
        List<Content> contents = contentRepository.findAllByLanguage("en");

        // then
        assertThat(contents).hasSize(3)
                .extracting("title", "subtitle", "description", "language")
                .containsExactlyInAnyOrder(
                        tuple("인턴", "비즈니스 표현 배우기", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en"),
                        tuple("셜록", "일상 표현 배우기", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en"),
                        tuple("왕좌의 게임", "전쟁 표현 배우기", "왕좌의 게임을 통해 전쟁 표현을 배워봅시다.", "en")
                );
    }

}