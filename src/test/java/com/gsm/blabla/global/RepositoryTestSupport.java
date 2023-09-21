package com.gsm.blabla.global;

import com.gsm.blabla.content.dao.ContentDetailRepository;
import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.dao.MemberContentDetailRepository;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import java.time.LocalTime;

import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public abstract class RepositoryTestSupport {

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    protected ContentRepository contentRepository;

    @Autowired
    protected ContentDetailRepository contentDetailRepository;

    @Autowired
    protected ScheduleRepository scheduleRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberContentDetailRepository memberContentDetailRepository;

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

    protected ContentDetail createContentDetail(Content content, String title, String description,
                                                LocalTime startedAt, LocalTime stoppedAt, LocalTime endedAt, Long sequence) {
        return contentDetailRepository.save(ContentDetail.builder()
                .content(content)
                .title(title)
                .description(description)
                .contentUrl("https://www.youtube.com/watch?v=sHpGT4SQwgw")
                .guideSentence("나는 오스틴 입니다. About the Fit의 창업자 입니다.")
                .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                .startedAt(startedAt)
                .stoppedAt(stoppedAt)
                .endedAt(endedAt)
                .sequence(sequence)
                .build());
    }

    protected Content createContent(String title, String description, String language, Long sequence) {
        return contentRepository.save(Content.builder()
                .title(title)
                .description(description)
                .language(language)
                .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
                .sequence(sequence)
                .build());
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
}
