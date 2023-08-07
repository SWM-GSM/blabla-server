package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.ApplyMessageRepository;
import com.gsm.blabla.crew.domain.ApplyMessage;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.crew.dto.MessageRequestDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

class CrewServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;

    @Autowired
    private CrewService crewService;

    @Autowired
    private ApplyMessageRepository applyMessageRepository;

    @BeforeEach
    void setUp() {
        member1 = createMember("cat");
        member2 = createMember("dog");
    }

    @DisplayName("[POST] 크루를 생성한다.")
    @Test
    @WithCustomMockUser
    void create() {
        // given
        long crewBefore = crewRepository.count();
        long crewMemberBefore = crewMemberRepository.count();

        CrewRequestDto crewRequestDto = CrewRequestDto.builder()
            .coverImage("test")
            .name("테스트")
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(8)
            .korLevel(1)
            .engLevel(1)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(true)
            .build();

        // when
        Long response = crewService.create(crewRequestDto).get("crewId");

        // then
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(response, 1L).orElseThrow(
            () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        assertThat(response).isEqualTo(crewBefore + 1);
        assertThat(crewMemberRepository.count()).isEqualTo(crewMemberBefore + 1);
        assertThat(crewMember.getMember().getId()).isEqualTo(member1.getId());
        assertThat(crewMember.getCrew().getId()).isEqualTo(response);

    }

    @DisplayName("[GET] 크루를 한국어 버전으로 조회한다.")
    @Test
    @WithCustomMockUser
    void get() {
        // given
        Long crewId = createCrew("테스트", true);

        // when
        CrewResponseDto response = crewService.get("ko", crewId);

        // then
        assertThat(response)
            .extracting(
                "name", "description", "meetingCycle", "maxNum",
                "korLevel", "korLevelText", "engLevel", "engLevelText", "preferMember", "detail", "autoApproval",
                "coverImage", "status", "members", "tags")
            .contains(
               "테스트", "테스트 크루입니다.", MeetingCycle.EVERYDAY.getName(), 8,
                1, Level.ONE_KOR.getDescription(), 1, Level.ONE_KOR.getDescription(), PreferMember.SAME_HOBBY.getKoreanName(), "테스트 크루입니다.", true,
                "test", "JOINED", 1, List.of(Tag.CULTURE.getKoreanName(), Tag.FILM_MUSIC.getKoreanName())
            );
    }

    @DisplayName("[GET] 크루 목록을 한국어 버전으로 조회한다.")
    @Test
    @WithCustomMockUser
    void getAll() {
        // given
        for (int i = 1; i <= 30 ; i++) {
            createCrew("테스트" + i, true);
        }
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CrewResponseDto> response = crewService.getAll("ko", pageable);

        // then
        assertThat(response.getContent()).hasSize(10);
        assertThat(response.getContent().get(0))
            .extracting("id", "name", "maxNum", "currentNum", "korLevel", "engLevel",
                "autoApproval", "coverImage", "createdAt", "tags")
            .contains(1L, "테스트1", 8, 1, 1, 1, true, "test", LocalDate.now().toString(), List.of(Tag.CULTURE.getKoreanName(), Tag.FILM_MUSIC.getKoreanName()));
    }

    @DisplayName("[GET] 나의 크루를 조회한다.")
    @Test
    @WithCustomMockUser
    void getMyCrews() {
        // given
        createCrew("테스트1", true);
        createCrew("테스트2", true);
        createCrew("테스트3", true);

        // when
        List<CrewResponseDto> response = crewService.getMyCrews().get("crews");

        // then
        assertThat(response).hasSize(3)
            .extracting("name")
            .containsExactlyInAnyOrder("테스트1", "테스트2", "테스트3");
    }

    @DisplayName("[POST] 크루 가입을 신청한다. 선착순일 경우, 가입 완료 처리된다.")
    @Test
    @WithCustomMockUser(id = "2")
    void joinAutoApprovalCrew() {
        // given
        Long crewId = createPreparedCrew(member1, true);

        // when
        String response = crewService.joinCrew(crewId, null).get("message");

        // then
        assertThat(response).isEqualTo("가입이 완료되었습니다.");
    }

    @DisplayName("[POST] 크루 가입을 신청한다. 승인제일 경우, 가입 대기 처리된다.")
    @Test
    @WithCustomMockUser(id = "2")
    void joinNonAutoApprovalCrew() {
        // given
        Long crewId = createPreparedCrew(member1, false);

        // when
        String response = crewService.joinCrew(crewId, MessageRequestDto.builder().message("테스트").build()).get("message");

        // then
        assertThat(response).isEqualTo("신청이 완료되었습니다.");
    }

    @DisplayName("[POST] 이미 크루에 가입한 유저는 가입 신청을 할 경우 예외가 발생한다.")
    @Test
    @WithCustomMockUser
    void joinAlreadyJoinedCrew() {
        // given
        Long crewId = createCrew("테스트", true);

        // when // then
        assertThatThrownBy(() -> crewService.joinCrew(crewId, MessageRequestDto.builder().message("테스트").build()))
            .isInstanceOf(GeneralException.class)
            .hasMessage("이미 가입한 크루입니다.");
    }

    @DisplayName("[GET] 크루 가입 승인 대기 인원을 조회한다.")
    @Test
    @WithCustomMockUser
    void getWaitingList() {
        // given
        Long crewId = createCrew("테스트", false);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        Member member3 = createMember("lion");
        Member member4 = createMember("bear");
        joinNonAutoApprovalCrew(member2, crew);
        joinNonAutoApprovalCrew(member3, crew);
        joinNonAutoApprovalCrew(member4, crew);

        // when
        List<MemberResponseDto> response = crewService.getWaitingList(crewId).get("members");

        // then
        assertThat(response).hasSize(3)
            .extracting("profileImage", "application")
            .containsExactlyInAnyOrder(
                tuple("dog", "테스트"),
                tuple("lion", "테스트"),
                tuple("bear", "테스트")
            );
    }

    @DisplayName("[GET] 크루장이 아닌 크루 멤버는 크루 가입 승인 대기 인원을 조회할 수 없다.")
    @Test
    void onlyLeadergetWaitingList() {
        // TODO: API 예외처리 추가 후 작성하기
        // given

        // when

        // then
        assertThat(true).isFalse();
    }

    @DisplayName("[DELETE] 크루를 탈퇴한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void withdrawal() {
        // given
        Long crewId = createPreparedCrew(member1, true);
        crewService.joinCrew(crewId, null);
        int countCrewMemberBefore = crewMemberRepository.countCrewMembersByCrewIdAndStatus(crewId, CrewMemberStatus.JOINED);

        // when
        String response = crewService.withdrawal(crewId).get("message");
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, 2L)
            .orElseThrow(() -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));
        int countCrewMemberAfter = crewMemberRepository.countCrewMembersByCrewIdAndStatus(crewId, CrewMemberStatus.JOINED);

        // then
        assertThat(response).isEqualTo("크루 탈퇴가 완료되었습니다.");
        assertThat(crewMember.getStatus()).isEqualTo(CrewMemberStatus.WITHDRAWAL);
        assertThat(crewMember.getWithdrawnAt()).isNotNull();
        assertThat(countCrewMemberAfter).isEqualTo(countCrewMemberBefore - 1);
    }

    @DisplayName("[DELETE] 크루장이 크루원을 강제 탈퇴한다.")
    @Test
    @WithCustomMockUser
    void forceWithdrawal() {
        // given
        Long crewId = createCrew("테스트", true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);
        int countCrewMemberBefore = crewMemberRepository.countCrewMembersByCrewIdAndStatus(crewId, CrewMemberStatus.JOINED);

        // when
        String response = crewService.forceWithdrawal(crewId, member2.getId()).get("message");
        int countCrewMemberAfter = crewMemberRepository.countCrewMembersByCrewIdAndStatus(crewId, CrewMemberStatus.JOINED);
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, member2.getId())
            .orElseThrow(() -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        // then
        assertThat(response).isEqualTo("강제 탈퇴가 완료되었습니다.");
        assertThat(countCrewMemberAfter).isEqualTo(countCrewMemberBefore - 1);
        assertThat(crewMember.getStatus()).isEqualTo(CrewMemberStatus.WITHDRAWAL);
        assertThat(crewMember.getWithdrawnAt()).isNotNull();
    }

    @DisplayName("[DELETE] 크루장이 아닌 크루 멤버는 크루원을 강제 탈퇴할 수 없다.")
    @Test
    @WithCustomMockUser(id = "2")
    void onlyLeaderCanWithdrawal() {
        // given
        Long crewId = createPreparedCrew(member1, true);
        crewService.joinCrew(crewId, null);

        // when // then
        assertThatThrownBy(() -> crewService.forceWithdrawal(crewId, member1.getId()))
            .isInstanceOf(GeneralException.class)
            .hasMessage("크루장만 강제 탈퇴를 할 수 있습니다.");
    }

    void joinNonAutoApprovalCrew(Member member, Crew crew) {
        applyMessageRepository.save(
            ApplyMessage.builder()
                .message("테스트")
                .crew(crew)
                .member(member)
                .build()
        );
    }
}
