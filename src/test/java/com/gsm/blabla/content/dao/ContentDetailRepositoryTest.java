package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContentDetailRepositoryTest {

    @AfterEach
    void afterEach() {
        contentDetailRepository.deleteAll();
        contentRepository.deleteAll();
    }

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentDetailRepository contentDetailRepository;

    @DisplayName("컨텐츠를 통해 세부 컨텐츠 리스트를 조회한다.")
    @Test
    void findAllByContent() {
        // given
        Content content1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en");
        Content content2 = createContent("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en");
        contentRepository.saveAll(List.of(content1, content2));

        ContentDetail contentDetail1 = getContentDetail(content1, "소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20));
        ContentDetail contentDetail2 = getContentDetail(content1, "조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20));
        ContentDetail contentDetail3 = getContentDetail(content2, "인사 배우기", "셜록을 통해 일상 표현을 배워봅시다.", LocalTime.of(14, 0, 0), LocalTime.of(14, 0, 10), LocalTime.of(14, 0, 20));
        contentDetailRepository.saveAll(List.of(contentDetail1, contentDetail2, contentDetail3));

        // when
        List<ContentDetail> contentDetails = contentDetailRepository.findAllByContent(content1);

        // then
        assertThat(contentDetails).hasSize(2)
                .extracting("title", "description", "startedAt", "stoppedAt", "endedAt")
                .containsExactlyInAnyOrder(
                        tuple("소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20)),
                        tuple("조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20))
                );
    }

    private static ContentDetail getContentDetail(Content content, String title, String description, LocalTime startedAt, LocalTime stoppedAt, LocalTime endedAt) {
        ContentDetail contentDetail = ContentDetail.builder()
            .content(content)
            .title(title)
            .description(description)
            .contentUrl("https://www.youtube.com/watch?v=sHpGT4SQwgw")
            .guideSentence("나는 오스틴 입니다. About the Fit의 창업자 입니다.")
            .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
            .startedAt(startedAt)
            .stoppedAt(stoppedAt)
            .endedAt(endedAt)
            .build();
        return contentDetail;
    }

    private static Content createContent(String title, String description, String language) {
        return Content.builder()
                .title(title)
                .description(description)
                .language(language)
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .build();
    }

}