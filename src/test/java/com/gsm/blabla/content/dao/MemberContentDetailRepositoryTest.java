package com.gsm.blabla.content.dao;

import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.global.RepositoryTestSupport;
import com.gsm.blabla.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class MemberContentDetailRepositoryTest extends RepositoryTestSupport {

    Member member;
    Content content1;
    ContentDetail contentDetail1;
    ContentDetail contentDetail2;
    ContentDetail contentDetail3;
    MemberContentDetail memberContentDetail1;
    MemberContentDetail memberContentDetail2;

    @BeforeEach
    void setUp() {
        member = createMember("유저", "cat");

        content1 = createContent("인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", "en", 300L);

        contentDetail1 = createContentDetail(content1, "소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.", LocalTime.of(12, 0, 0), LocalTime.of(12, 0, 10), LocalTime.of(12, 0, 20), 200L);
        contentDetail2 = createContentDetail(content1, "조언해주기", "인턴을 통해 조언해주기를 배워봅시다.", LocalTime.of(13, 0, 0), LocalTime.of(13, 0, 10), LocalTime.of(13, 0, 20), 100L);
        contentDetail3 = createContentDetail(content1, "인사 배우기", "인턴을 통해 인사를 배워봅시다.", LocalTime.of(14, 0, 0), LocalTime.of(14, 0, 10), LocalTime.of(14, 0, 20), 300L);

        memberContentDetail1 = createMemberContentDetail(member, contentDetail1);
        memberContentDetail2 = createMemberContentDetail(member, contentDetail2);
    }

    @DisplayName("유저가 세부 컨텐츠를 학습했는지 조회한다.")
    @CsvSource({"1, true, 유저가 세부 컨텐츠를 학습했다.", "3, false, 유저가 세부 컨텐츠를 학습하지 않았다."})
    @ParameterizedTest(name = "{2}")
    void findByContentDetailIdAndMemberId(Long contentDetailId, Boolean expectedResult, String _description) {
        // given

        // when
        Optional<MemberContentDetail> memberContentDetail =
                memberContentDetailRepository.findByContentDetailIdAndMemberId(contentDetailId, member.getId());

        // then
        assertThat(memberContentDetail.isPresent()).isEqualTo(expectedResult);
    }

    @DisplayName("유저가 학습한 세부 컨텐츠를 조회한다.")
    @Test
    void findAllByMemberId() {
        // given

        // when
        List<MemberContentDetail> memberContentDetail = memberContentDetailRepository.findAllByMemberId(member.getId());

        // then
        assertThat(memberContentDetail).hasSize(2)
                .extracting("member", "contentDetail")
                .containsExactly(
                        tuple(member, contentDetail1),
                        tuple(member, contentDetail2)
                );
        assertThat(memberContentDetail.get(0).getContentDetail())
                .extracting("title", "description", "startedAt", "stoppedAt", "endedAt", "sequence")
                .containsExactly("소개 인사하기", "인턴을 통해 소개 인사를 배워봅시다.",
                        LocalTime.of(12, 0, 0),
                        LocalTime.of(12, 0, 10),
                        LocalTime.of(12, 0, 20), 200L);
        assertThat(memberContentDetail.get(1).getContentDetail())
                .extracting("title", "description", "startedAt", "stoppedAt", "endedAt", "sequence")
                .containsExactly("조언해주기", "인턴을 통해 조언해주기를 배워봅시다.",
                        LocalTime.of(13, 0, 0),
                        LocalTime.of(13, 0, 10),
                        LocalTime.of(13, 0, 20), 100L);
    }
}