package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.global.RepositoryTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ContentDetailRepositoryTest extends RepositoryTestSupport {

    @DisplayName("컨텐츠를 통해 세부 컨텐츠 리스트를 조회한다.")
    @Test
    void findAllByContent() {
        // given
        Content content1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en", 3L);
        Content content2 = createContent("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en", 1L);
        contentRepository.saveAll(List.of(content1, content2));

        ContentDetail contentDetail1 = createContentDetail(content1, "소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20), 2L);
        ContentDetail contentDetail2 = createContentDetail(content1, "조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20), 1L);
        ContentDetail contentDetail3 = createContentDetail(content2, "인사 배우기", "셜록을 통해 일상 표현을 배워봅시다.", LocalTime.of(14, 0, 0), LocalTime.of(14, 0, 10), LocalTime.of(14, 0, 20), 2L);
        contentDetailRepository.saveAll(List.of(contentDetail1, contentDetail2, contentDetail3));

        // when
        List<ContentDetail> contentDetails = contentDetailRepository.findAllByContent(content1);

        // then
        assertThat(contentDetails).hasSize(2)
                .extracting("title", "description", "startedAt", "stoppedAt", "endedAt")
                .containsExactly(
                        tuple("소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20)),
                        tuple("조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20))
                );
    }

    @DisplayName("컨텐츠를 통해 세부 컨텐츠 리스트를 순서대로 조회한다.")
    @Test
    void findAllByContentOrderBySequence() {
        // given
        Content content1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en", 3L);
        Content content2 = createContent("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en", 1L);
        contentRepository.saveAll(List.of(content1, content2));

        ContentDetail contentDetail1 = createContentDetail(content1, "소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20), 2L);
        ContentDetail contentDetail2 = createContentDetail(content1, "조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20), 1L);
        ContentDetail contentDetail3 = createContentDetail(content2, "인사 배우기", "셜록을 통해 일상 표현을 배워봅시다.", LocalTime.of(14, 0, 0), LocalTime.of(14, 0, 10), LocalTime.of(14, 0, 20), 2L);
        contentDetailRepository.saveAll(List.of(contentDetail1, contentDetail2, contentDetail3));

        // when
        List<ContentDetail> contentDetails = contentDetailRepository.findAllByContentOrderBySequence(content1);

        // then
        assertThat(contentDetails).hasSize(2)
            .extracting("title", "description", "startedAt", "stoppedAt", "endedAt")
            .containsExactly(
                tuple("조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20)),
                tuple("소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20))
            );
    }

}
