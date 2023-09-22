package com.gsm.blabla.global;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.agora.dao.AccuseRepository;
import com.gsm.blabla.agora.dao.VoiceRoomRepository;
import com.gsm.blabla.agora.domain.VoiceRoom;
import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.content.application.ContentService;
import com.gsm.blabla.content.dao.ContentDetailRepository;
import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.dao.MemberContentDetailRepository;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.CrewReportKeywordRepository;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.CrewReportKeyword;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.auth.dao.AppleAccountRepository;
import com.gsm.blabla.auth.dao.GoogleAccountRepository;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    protected AgoraService agoraService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected GoogleAccountRepository googleAccountRepository;

    @Autowired
    protected AppleAccountRepository appleAccountRepository;

    @Autowired
    protected CrewReportRepository crewReportRepository;

    @Autowired
    protected VoiceFileRepository voiceFileRepository;

    @Autowired
    protected CrewReportAnalysisRepository crewReportAnalysisRepository;

    @Autowired
    private CrewReportKeywordRepository crewReportKeywordRepository;

    @Autowired
    protected ContentRepository contentRepository;

    @Autowired
    protected ContentDetailRepository contentDetailRepository;

    @Autowired
    protected MemberContentDetailRepository memberContentDetailRepository;

    @Autowired
    protected VoiceRoomRepository voiceRoomRepository;

    @Autowired
    protected AccuseRepository accuseRepository;

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
        joinVoiceRoom(member2);
        exitVoiceRoom(member1, crewReport);
        exitVoiceRoom(member2, crewReport);
        createReportAnalysis(crewReport);

        return crewReport;
    }

    protected void joinVoiceRoom(Member member) {
        Long memberId = member.getId();
        boolean inVoiceRoom = voiceRoomRepository.existsByMemberId(memberId);
        if (inVoiceRoom) {
            VoiceRoom voiceRoom = voiceRoomRepository.findByMemberId(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_IN_VOICE_ROOM, "보이스룸에 접속하지 않은 유저입니다.")
            );
            voiceRoom.updateInVoiceRoom(true);
        } else {
            voiceRoomRepository.save(
                VoiceRoom.builder()
                    .member(member)
                    .inVoiceRoom(true)
                    .build()
            );
        }
    }

    protected CrewReport startVoiceRoom(LocalDateTime startedAt, Member member) {
        joinVoiceRoom(member);

        return crewReportRepository.save(
            CrewReport.builder()
                .startedAt(startedAt)
                .endAt(startedAt.plusMinutes(26).plusSeconds(30))
                .member(member)
                .build()
        );
    }

    protected void createMemberContentDetail(Member member) {
        Content content = contentRepository.save(
            Content.builder()
                .title("주토피아")
                .description("꿈과 희망의 나라 주토피아")
                .language("ko")
                .thumbnailURL("www.test.com")
                .build()
        );

        ContentDetail contentDetail = contentDetailRepository.save(
            ContentDetail.builder()
                .content(content)
                .title("다짐하는 표현")
                .description("주디가 주토피아 경찰을 대표하여 표창을 받는다.")
                .guideSentence("실망시키지 않겠습니다.")
                .targetSentence("I won't let you down.")
                .contentUrl("www.test.com")
                .startedAt(LocalTime.of(0, 0, 0))
                .stoppedAt(LocalTime.of(0, 0, 1))
                .endedAt(LocalTime.of(0, 0, 2))
                .build()
        );

        memberContentDetailRepository.save(
            MemberContentDetail.builder()
                .member(member)
                .contentDetail(contentDetail)
                .userAnswer("test")
                .longFeedback("test")
                .starScore(3.0)
                .contextScore(3.0)
                .build()
        );
    }

    protected ContentDetail createContentDetail(Content content, String title, String contentUrl, Long sequence) {
        return contentDetailRepository.save(
            ContentDetail.builder()
                .content(content)
                .title(title)
                .description("CEO 오스틴의 연설을 통하여 비즈니스 표현을 배워보세요.")
                .contentUrl(contentUrl)
                .guideSentence("나는 오스틴 입니다. About the Fit의 창업자 입니다.")
                .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                .startedAt(LocalTime.of(0, 0, 0))
                .stoppedAt(LocalTime.of(0, 0, 1))
                .endedAt(LocalTime.of(0, 0, 2))
                .sequence(sequence)
                .build()
        );
    }

    protected Content createContent(String title, String description, String language, Long sequence) {
        return contentRepository.save(
            Content.builder()
                .title(title)
                .description(description)
                .language(language)
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .sequence(sequence)
                .build()
        );
    }

    protected MemberContentDetail createMemberContentDetail(Member member, ContentDetail contentDetail) {
        return memberContentDetailRepository.save(
            MemberContentDetail.builder()
                .member(member)
                .contentDetail(contentDetail)
                .userAnswer("test")
                .longFeedback("test")
                .starScore(3.0)
                .contextScore(3.0)
                .build()
        );
    }

    private void updateInVoiceRoom(Member member) {
        VoiceRoom voiceRoom = voiceRoomRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new GeneralException(com.gsm.blabla.global.response.Code.MEMBER_NOT_IN_VOICE_ROOM, "보이스룸에 접속하지 않은 유저입니다.")
        );
        voiceRoom.updateInVoiceRoom(false);
    }

    private void saveVoiceFile(Member member, CrewReport crewReport) {
        VoiceFile voiceFile = voiceFileRepository.save(
            VoiceFile.builder()
                .member(member)
                .crewReport(crewReport)
                .fileUrl("www.test.com")
                .feedback("테스트 피드백 by " + member.getProfileImage())
                .build()
        );

        voiceFile.createFeedback("테스트 피드백");
    }

    private void exitVoiceRoom(Member member, CrewReport crewReport) {
        updateInVoiceRoom(member);
        saveVoiceFile(member, crewReport);
    }

    private void createReportAnalysis(CrewReport crewReport) {
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

    private void createKeyword(CrewReport crewReport, String keyword, Long count) {
        crewReportKeywordRepository.save(
            CrewReportKeyword.builder()
                .crewReport(crewReport)
                .keyword(keyword)
                .count(count)
                .build()
        );
    }
}
