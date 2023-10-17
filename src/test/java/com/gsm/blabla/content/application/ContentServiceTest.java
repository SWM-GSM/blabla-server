package com.gsm.blabla.content.application;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.content.dto.ContentDetailResponseDto;
import com.gsm.blabla.content.dto.ContentDetailsResponseDto;
import com.gsm.blabla.content.dto.ContentsResponseDto;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ContentServiceTest extends IntegrationTestSupport {

    Content contentEn1;
    Content contentEn2;
    Content contentKo1;
    Content contentKo2;


    ContentDetail contentDetailEn1;
    ContentDetail contentDetailEn2;
    ContentDetail contentDetailEn3;
    ContentDetail contentDetailEn4;
    ContentDetail contentDetailKo1;
    ContentDetail contentDetailKo2;
    ContentDetail contentDetailKo3;

    Member member;
    MemberContentDetail memberContentDetail1;
    MemberContentDetail memberContentDetail2;
    MemberContentDetail memberContentDetail3;

    @BeforeEach
    void setUp() {
        member = createMember("테스트", "cat");

        contentEn1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en", 3L);
        contentEn2 = createContent("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", "en", 1L);
        contentKo1 = createContent("BTS", "BTS를 통해 한국어 발음과 일상 표현을 배워봅시다.", "ko", 4L);
        contentKo2 = createContent("Parasite", "영화 기생충을 통해 한국어 발음과 일상 표현을 배워봅시다.", "ko", 5L);

        contentDetailEn1 = createContentDetail(contentEn1, "소개 인사하기", "https://youtu.be/testEn1",200L);
        contentDetailEn2 = createContentDetail(contentEn1, "조언해주기", "https://youtu.be/testEn2",100L);
        contentDetailEn3 = createContentDetail(contentEn1, "조언하기", "https://youtu.be/testEn3", 500L);
        contentDetailEn4 = createContentDetail(contentEn1, "연설하기", "https://youtu.be/testEn4", 600L);
        contentDetailKo1 = createContentDetail(contentKo1, "인사 배우기", "https://youtu.be/testKo1", 200L);
        contentDetailKo2 = createContentDetail(contentKo1, "영화 소개하기", "https://youtu.be/testKo2", 100L);
        contentDetailKo3 = createContentDetail(contentKo1, "연설하기", "https://youtu.be/testKo3", 300L);

        memberContentDetail1 = createMemberContentDetail(member, contentDetailEn1);
        memberContentDetail2 = createMemberContentDetail(member, contentDetailKo1);
        memberContentDetail3 = createMemberContentDetail(member, contentDetailKo3);
    }

    @DisplayName("영어 컨텐츠 리스트를 조회한다.")
    @WithCustomMockUser
    @Test
    void getContents() {
        // given

        // when
        Map<String, List<ContentsResponseDto>> contents = contentService.getContents("en");

        // then
        assertThat(contents).containsKey("contents");
        assertThat(contents.get("contents")).hasSize(2)
                .extracting("title", "description", "progress")
                .containsExactly(
                        tuple("셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", 0.0),
                        tuple("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", 25.0)
                );
    }


    @DisplayName("세부 컨텐츠 리스트를 조회한다.")
    @WithCustomMockUser
    @Test
    void getContentDetails() {
        // given

        // when
        ContentDetailsResponseDto contentDetailsResponseDto = contentService.getContentDetails(1L);

        // then
        assertThat(contentDetailsResponseDto).extracting("title", "description")
                .containsExactly("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.");

        assertThat(contentDetailsResponseDto.getContentDetails()).hasSize(4)
                .extracting("title", "isCompleted", "thumbnailUrl")
                .containsExactly(
                        tuple("조언해주기", false, "https://img.youtube.com/vi/testEn2/hqdefault.jpg"),
                        tuple("소개 인사하기", true, "https://img.youtube.com/vi/testEn1/hqdefault.jpg"),
                        tuple("조언하기", false, "https://img.youtube.com/vi/testEn3/hqdefault.jpg"),
                        tuple("연설하기", false, "https://img.youtube.com/vi/testEn4/hqdefault.jpg")
                );
    }

    @DisplayName("세부 컨텐츠를 조회한다.")
    @Test
    void getContentDetail() {
        // given
        ContentDetail contentDetailEn= ContentDetail.builder()
                .title("소개 인사하기")
                .description("인턴을 통해 소개 인사를 배워봅시다.")
                .startedAt(LocalTime.of(12, 0, 0))
                .stoppedAt(LocalTime.of(12, 0, 10))
                .endedAt(LocalTime.of(12, 0, 20))
                .guideSentence("나는 오스틴 입니다. About the Fit의 창업자 입니다.")
                .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                .contentUrl("https://www.youtu.be/sHpGT4SQwgw")
                .sequence(2L)
                .content(contentEn1)
                .build();
        contentDetailRepository.save(contentDetailEn);

        // when
        ContentDetailResponseDto contentDetailResponseDto = contentService.getContentDetail(contentDetailEn.getId());

        // then
        assertThat(contentDetailResponseDto)
                .extracting("title", "description", "contentUrl", "guideSentence", "targetSentence", "startedAtSec", "stoppedAtSec", "endedAtSec")
                .containsExactly("소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", "https://www.youtu.be/sHpGT4SQwgw", "나는 오스틴 입니다. About the Fit의 창업자 입니다.", "I'm Jules Ostin. I'm the founder of About the Fit.", 43200, 43210, 43220);
    }

}
