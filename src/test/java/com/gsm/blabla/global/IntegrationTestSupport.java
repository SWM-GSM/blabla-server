package com.gsm.blabla.global;

import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.SocialLoginType;
import java.time.LocalDate;
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
}
