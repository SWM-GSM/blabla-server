package com.gsm.blabla.global;

import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.CrewReportKeywordRepository;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.CrewReportKeyword;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.auth.dao.AppleAccountRepository;
import com.gsm.blabla.auth.dao.GoogleAccountRepository;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=local"
)
public abstract class IntegrationTestSupport {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    protected CrewService crewService;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected GoogleAccountRepository googleAccountRepository;

    @Autowired
    protected AppleAccountRepository appleAccountRepository;

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

    @Autowired
    private CrewReportKeywordRepository crewReportKeywordRepository;

    @AfterEach
    void cleanUpDatabase() {
        databaseCleanup.execute();
    }

    protected Member createMember(String nickname, String profileImage) {
        return memberRepository.save(Member.builder()
            .socialLoginType(SocialLoginType.TEST)
            .nickname(nickname)
            .profileImage(profileImage)
            .learningLanguage("ko")
            .build()
        );
    }

    protected CrewReport createReport(Member member1, Member member2, LocalDateTime startedAt) {
        CrewReport crewReport = startVoiceRoom(startedAt, member1);
        exitVoiceRoom(member1, crewReport);
        exitVoiceRoom(member2, crewReport);
        createReportAnalysis(crewReport);

        return crewReport;
    }

    protected CrewReport startVoiceRoom(LocalDateTime startedAt, Member member) {
        return crewReportRepository.save(
            CrewReport.builder()
                .startedAt(startedAt)
                .endAt(startedAt.plusMinutes(26).plusSeconds(30))
                .member(member)
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

    protected void createReportAnalysis(CrewReport crewReport) {
        crewReportAnalysisRepository.save(
            CrewReportAnalysis.builder()
                .crewReport(crewReport)
                .koreanTime(Duration.ofMinutes(20))
                .englishTime(Duration.ofMinutes(6))
                .cloudUrl("www.test.com")
                .build()
        );

        createKeyword(crewReport, "테스트1", 38L);
        createKeyword(crewReport, "테스트2", 30L);
        createKeyword(crewReport, "테스트3", 20L);
    }

    protected void createKeyword(CrewReport crewReport, String keyword, Long count) {
        crewReportKeywordRepository.save(
            CrewReportKeyword.builder()
                .crewReport(crewReport)
                .keyword(keyword)
                .count(count)
                .build()
        );
    }
}
