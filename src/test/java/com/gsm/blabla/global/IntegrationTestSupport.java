package com.gsm.blabla.global;

import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    protected CrewService crewService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CrewRepository crewRepository;

    @Autowired
    protected CrewMemberRepository crewMemberRepository;

    @Autowired
    protected CrewTagRepository crewTagRepository;

    @Autowired
    private CrewReportRepository crewReportRepository;

    @Autowired
    private VoiceFileRepository voiceFileRepository;

    @Autowired
    private CrewReportAnalysisRepository crewReportAnalysisRepository;

    @AfterEach
    void cleanUpDatabase() {
        databaseCleanup.execute();
    }

    protected Member createMember(String profileImage) {
        return memberRepository.save(Member.builder()
            .socialLoginType(SocialLoginType.TEST)
            .nickname("테스트")
            .profileImage(profileImage)
            .birthDate(LocalDate.parse("2001-01-01"))
            .gender("male")
            .countryCode("KR")
            .korLevel(3)
            .engLevel(3)
            .pushNotification(false)
            .build()
        );
    }

    protected Long createCrew(String name, boolean autoApproval) {
        return crewService.create(CrewRequestDto.builder()
            .coverImage("test")
            .name(name)
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(8)
            .korLevel(1)
            .engLevel(1)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(autoApproval)
            .build()).get("crewId");
    }

    protected Long createPreparedCrew(Member member, String name, int maxNum, int korLevel, int engLevel, boolean autoApproval) {
        CrewRequestDto crewRequestDto = CrewRequestDto.builder()
            .coverImage("test")
            .name(name)
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(maxNum)
            .korLevel(korLevel)
            .engLevel(engLevel)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(autoApproval)
            .build();

        Crew crew = crewRepository.save(crewRequestDto.toEntity());

        crewRequestDto.getTags().forEach(tag ->
            crewTagRepository.save(CrewTag.builder()
                .crew(crew)
                .tag(tag)
                .build()
            )
        );

        crewMemberRepository.save(CrewMember.builder()
            .crew(crew)
            .member(member)
            .role(CrewMemberRole.LEADER)
            .build()
        );

        return crew.getId();
    }

    protected void joinCrew(Member member, Crew crew) {
        crewMemberRepository.save(
            CrewMember.builder()
                .member(member)
                .crew(crew)
                .role(CrewMemberRole.MEMBER)
                .build()
        );
    }

    protected CrewReport startVoiceRoom(Crew crew, LocalDateTime startedAt) {
        return crewReportRepository.save(
            CrewReport.builder()
                .crew(crew)
                .startedAt(startedAt)
                .build()
        );
    }

    protected void exitVoiceRoom(Member member, CrewReport crewReport) {
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

    protected void createReportAnalysis(CrewReport crewReport, LocalDateTime startedAt) {
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

    public CrewReport createReport(Member member1, Member member2, Crew crew, LocalDateTime startedAt) {
        CrewReport crewReport = startVoiceRoom(crew, startedAt);
        exitVoiceRoom(member1, crewReport);
        exitVoiceRoom(member2, crewReport);
        createReportAnalysis(crewReport, startedAt);

        return crewReport;
    }
}
