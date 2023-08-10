package com.gsm.blabla.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.dao.ApplyMessageRepository;
import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.ApplyMessage;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.crew.dto.CrewReportResponseDto;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.crew.dto.MessageRequestDto;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
class CrewServiceTest extends IntegrationTestSupport {

    Member member1;
    Member member2;
    LocalDateTime now = LocalDateTime.now();

    @Autowired
    private CrewService crewService;

    @Autowired
    private ApplyMessageRepository applyMessageRepository;

    @Autowired
    private CrewReportRepository crewReportRepository;

    @Autowired
    private VoiceFileRepository voiceFileRepository;

    @Autowired
    private CrewReportAnalysisRepository crewReportAnalysisRepository;

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

    @DisplayName("[GET] 지금 참여 가능한 크루를 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getCanJoinCrews() {
        // given
        Member member3 = createMember("lion");
        Member member4 = createMember("tiger");

        createCrew("테스트1", true);
        createPreparedCrew(member1, "테스트2", 1, 1, 1, true); // 인원이 다 찬 크루
        createPreparedCrew(member1, "테스트3", 1, 5, 1, true); // 영어 최소 레벨 미충족
        createPreparedCrew(member1, "테스트4", 1, 1, 5, true); // 한국어 최소 레벨 미충족
        createPreparedCrew(member1, "테스트5", 8, 1, 1, true);
        createPreparedCrew(member1, "테스트6", 8, 1, 1, false); // 자동 승인이 아님
        createPreparedCrew(member1, "테스트7", 8, 1, 1, true);
        createPreparedCrew(member1, "테스트8", 8, 1, 1, false);
        createPreparedCrew(member1, "테스트9", 8, 1, 1, true);

        Long crewId14 = createPreparedCrew(member1, "테스트14", 8, 1, 1, true);
        joinCrew(member3, crewRepository.getReferenceById(crewId14));
        joinCrew(member4, crewRepository.getReferenceById(crewId14));

        Long crewId15 = createPreparedCrew(member1, "테스트15", 8, 1, 1, true);
        joinCrew(member3, crewRepository.getReferenceById(crewId15));
        joinCrew(member4, crewRepository.getReferenceById(crewId15));

        Long crewId11 = createPreparedCrew(member1, "테스트11", 8, 1, 1, false);
        joinCrew(member3, crewRepository.getReferenceById(crewId11));

        Long crewId10 = createPreparedCrew(member1, "테스트10", 8, 1, 1, true);
        joinCrew(member3, crewRepository.getReferenceById(crewId10));

        Long crewId12 = createPreparedCrew(member1, "테스트12", 8, 1, 1, true);
        joinCrew(member3, crewRepository.getReferenceById(crewId12));

        Long crewId13 = createPreparedCrew(member1, "테스트13", 8, 1, 1, true);
        joinCrew(member3, crewRepository.getReferenceById(crewId13));

        // when
        List<CrewResponseDto> response = crewService.getCanJoinCrews().get("crews");

        // then
        assertThat(response).hasSize(10)
            .extracting("name")
            .containsExactly("테스트14", "테스트15", "테스트10", "테스트12", "테스트13", "테스트5", "테스트7", "테스트9", "테스트11", "테스트6");
    }

    @DisplayName("[POST] 크루 가입을 신청한다. 선착순일 경우, 가입 완료 처리된다.")
    @Test
    @WithCustomMockUser(id = "2")
    void joinAutoApprovalCrew() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);

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
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, false);

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
    @WithCustomMockUser(id = "2")
    void onlyLeadergetWaitingList() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, false);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        Member member3 = createMember("lion");
        Member member4 = createMember("bear");
        joinNonAutoApprovalCrew(member2, crew);
        joinNonAutoApprovalCrew(member3, crew);
        joinNonAutoApprovalCrew(member4, crew);

        acceptMember(crew.getId(), member2.getId());

        // when // then
        assertThatThrownBy(() -> crewService.getWaitingList(crewId))
            .isInstanceOf(GeneralException.class)
            .hasMessage("크루장만 가입 승인 대기 인원을 조회할 수 있습니다.");
    }

    @DisplayName("[DELETE] 크루를 탈퇴한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void withdrawal() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
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

    @DisplayName("[DELETE] 크루장은 크루를 탈퇴할 수 없다.")
    @Test
    @WithCustomMockUser
    void leaderCannotWithdraw() {
        // given
        Long crewId = createCrew("테스트", true);

        // when // then
        assertThatThrownBy(() -> crewService.withdrawal(crewId))
            .isInstanceOf(GeneralException.class)
            .hasMessage("크루장은 크루를 탈퇴할 수 없습니다.");
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
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        crewService.joinCrew(crewId, null);

        // when // then
        assertThatThrownBy(() -> crewService.forceWithdrawal(crewId, member1.getId()))
            .isInstanceOf(GeneralException.class)
            .hasMessage("크루장만 강제 탈퇴를 할 수 있습니다.");
    }

    @DisplayName("[GET] 크루 리포트를 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getReport() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);
        CrewReport crewReport = createReport(crew, now);

        String nowToString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String durationTimeToString = String.format("%02d:%02d:%02d", 0, 26, 30);

        // when
        CrewReportResponseDto response = crewService.getReport(crewReport.getId());

        // then
        assertThat(response.getInfo()).extracting("createdAt")
            .matches(createdAt -> Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}", createdAt.toString()));
        assertThat(response.getInfo()).containsEntry("durationTime", durationTimeToString);
        assertThat(response.getMembers()).hasSize(2)
            .extracting("profileImage")
            .containsExactlyInAnyOrder("cat", "dog");
        assertThat(response.getBubbleChart()).isEqualTo("www.test.com");
        // TODO: CrewService 로직 추가 후 테스트 코드 수정하기
//        assertThat(response.getKeyword()).extracting("name")
//            .containsExactlyInAnyOrder("테스트1", "테스트2", "테스트3");
        assertThat(response.getKeyword()).isEmpty();
        assertThat(response.getLanguageRatio()).containsEntry("korean", 76);
        assertThat(response.getLanguageRatio()).containsEntry("english", 24);
        assertThat(response.getFeedbacks()).hasSize(2)
            .extracting("comment")
            .containsExactlyInAnyOrder("테스트 피드백 by cat", "테스트 피드백 by dog");
    }

    // TODO: generated = false인 경우에 대해 테스트 케이스 추가
    @DisplayName("[GET] 크루 리포트 목록을 조회한다.")
    @Test
    @WithCustomMockUser(id = "2")
    void getAllReports() {
        // given
        Long crewId = createPreparedCrew(member1, "테스트", 8, 1, 1, true);
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다."));
        joinCrew(member2, crew);

        CrewReport crewReport1 = createReport(crew, now); // 리포트 1
        CrewReport crewReport2 = createReport(crew, now.plusDays(1)); // 리포트 2
        CrewReport crewReport3 = createReport(crew, now.plusDays(2)); // 리포트 3

        // when
        List<CrewReportResponseDto> response = crewService.getAllReports(crewId, "desc").get("reports");

        // then
        assertThat(response).hasSize(3)
            .extracting("id", "generated")
            .containsExactlyInAnyOrder(
                tuple(crewReport1.getId(), true),
                tuple(crewReport2.getId(), true),
                tuple(crewReport3.getId(), true)
            );
        // TODO: createdAt 검증 좀 더 구체적으로
        assertThat(response).hasSize(3)
            .extracting("info")
            .extracting("createdAt")
            .allMatch(createdAt -> Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}", createdAt.toString()));
        assertThat(response).hasSize(3)
            .extracting("info")
            .extracting("durationTime")
            .containsExactlyInAnyOrder("00:26:30", "00:26:30", "00:26:30");
        // TODO: members에 대한 검증 추가
    }

    private void joinNonAutoApprovalCrew(Member member, Crew crew) {
        applyMessageRepository.save(
            ApplyMessage.builder()
                .message("테스트")
                .crew(crew)
                .member(member)
                .build()
        );
    }

    private void acceptMember(Long crewId, Long memberId) {
        ApplyMessage applyMessage = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId)
            .orElseThrow(
                () -> new GeneralException(Code.APPLY_NOT_FOUND, "존재하지 않는 신청입니다.")
            );
        applyMessage.acceptOrReject("accept");
        crewMemberRepository.save(CrewMember.builder()
            .member(applyMessage.getMember())
            .crew(applyMessage.getCrew())
            .role(CrewMemberRole.MEMBER)
            .build()
        );
    }

    private CrewReport startVoiceRoom(Crew crew, LocalDateTime startedAt) {
        return crewReportRepository.save(
            CrewReport.builder()
                .crew(crew)
                .startedAt(startedAt)
                .build()
        );
    }

    private void exitVoiceRoom(Member member, CrewReport crewReport) {
        VoiceFile voiceFile = voiceFileRepository.save(
            VoiceFile.builder()
                .member(member)
                .crewReport(crewReport)
                .fileUrl("www.test.com")
                .totalCallTime(Duration.ofMinutes(26).plusSeconds(30))
                .koreanTime(Duration.ofMinutes(20))
                .englishTime(Duration.ofMinutes(6))
                .redundancyTime(Duration.ofSeconds(30))
                .feedback("테스트 피드백 by " + member.getProfileImage())
                .build()
        );

        voiceFile.createFeedback("테스트 피드백 ");
    }

    private void createReportAnalysis(CrewReport crewReport, LocalDateTime startedAt) {
        crewReportAnalysisRepository.save(
            CrewReportAnalysis.builder()
                .crewReport(crewReport)
                .koreanTime(Duration.ofMinutes(20))
                .englishTime(Duration.ofMinutes(6))
                .cloudUrl("www.test.com")
                .endAt(startedAt.plusMinutes(26).plusSeconds(30))
                .build()
        );

        // TODO: 키워드 엔티티 생성 로직
    }

    private CrewReport createReport(Crew crew, LocalDateTime startedAt) {
        CrewReport crewReport = startVoiceRoom(crew, startedAt);
        exitVoiceRoom(member1, crewReport);
        exitVoiceRoom(member2, crewReport);
        createReportAnalysis(crewReport, startedAt);

        return crewReport;
    }
}
