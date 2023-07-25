package com.gsm.blabla.dummy.api;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.dummy.dto.AccuseDto;
import com.gsm.blabla.dummy.dto.CrewDto;
import com.gsm.blabla.dummy.dto.JoinDto;
import com.gsm.blabla.dummy.dto.KeywordDto;
import com.gsm.blabla.dummy.dto.MemberDto;
import com.gsm.blabla.dummy.dto.ProfileDto;
import com.gsm.blabla.dummy.dto.ReportDto;
import com.gsm.blabla.dummy.dto.ReportDto.Info;
import com.gsm.blabla.dummy.dto.ReportDto.LanguageRatio;
import com.gsm.blabla.dummy.dto.ScheduleDto;
import com.gsm.blabla.dummy.dto.StatusDto;
import com.gsm.blabla.dummy.dto.VoiceRoomDto;
import com.gsm.blabla.global.response.DataResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dummy")
public class DummyController {

    private final AgoraService agoraService;

    @GetMapping(value = "{language}/profile/{memberId}")
    public DataResponseDto<MemberDto> getProfile(@PathVariable String language) {
        List<KeywordDto> keywords = "ko".equals(language) ?
            List.of(
                KeywordDto.builder().emoji(Keyword.DRIVE.getEmoji()).name(Keyword.GAME.getKoreanName()).build(),
                KeywordDto.builder().emoji(Keyword.GAME.getEmoji()).name(Keyword.GAME.getKoreanName()).build()
            ) :
            List.of(
                KeywordDto.builder().emoji(Keyword.DRIVE.getEmoji()).name(Keyword.GAME.getEnglishName()).build(),
                KeywordDto.builder().emoji(Keyword.GAME.getEmoji()).name(Keyword.GAME.getEnglishName()).build()
            );

        return DataResponseDto.of(
            MemberDto.builder()
                .profileImage("cat")
                .nickname("가나다라마바사")
                .korLevel(5)
                .engLevel(3)
                .signedUpAfter(99)
                .countryCode("KR")
                .keywords(keywords)
                .description("안녕하세요, 반갑습니다. Nice to meet ya!")
                .build()
        );
    }

    @GetMapping(value = "/{language}/crews/{crewId}")
    public DataResponseDto<CrewDto> getCrew(@PathVariable String language) {
        MemberDto member1 = MemberDto.builder().id(1L).nickname("감자").description("안녕하세요 ㅎㅎ").profileImage("cat").countryCode("KR").korLevel(5).engLevel(3).isLeader(true).build();
        MemberDto member2 = MemberDto.builder().id(2L).nickname("고구마").description("안녕하세요 ㅋㅋ").profileImage("dog").countryCode("KR").korLevel(4).engLevel(2).isLeader(false).build();

        List<String> tags = "ko".equals(language) ? List.of(Tag.GAME.getKoreanName(), Tag.FILM_MUSIC.getKoreanName())
            : List.of(Tag.GAME.getEnglishName(), Tag.FILM_MUSIC.getEnglishName());

        return DataResponseDto.of(
            CrewDto.builder()
                .name("일요일마다 언어 교환할 분들 구함 :)")
                .description("같이 게임도 하고 프리토킹도 나눌 수 있으면 좋을 것 같아요 ㅎㅎ")
                .meetingCycle(MeetingCycle.EVERYDAY.getName())
                .maxNum(5)
                .currentNum(2)
                .korLevel(1)
                .korLevelText("ko".equals(language) ? Level.ONE_KOR.getDescription() : Level.ONE_ENG.getDescription())
                .engLevel(1)
                .engLevelText("ko".equals(language) ? Level.ONE_KOR.getDescription() : Level.ONE_ENG.getDescription())
                .preferMember("ko".equals(language) ? PreferMember.SAME_HOBBY.getKoreanName() : PreferMember.SAME_HOBBY.getEnglishName())
                .detail("같이 게임도 하고 프리토킹도 나눌 수 있으면 좋을 것 같아요 ㅎㅎ")
                .autoApproval(true)
                .coverImage("hello")
                .status(CrewMemberStatus.JOINED)
                .members(List.of(member1, member2))
                .tags(tags)
                .build()
        );
    }

    @GetMapping(value = "/{language}/crews")
    public DataResponseDto<Page<CrewDto>> getCrews(@PathVariable String language,
        @PageableDefault Pageable pageable) {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<String> tags = "ko".equals(language) ? List.of(Tag.GAME.getKoreanName(), Tag.FILM_MUSIC.getKoreanName())
            : List.of(Tag.GAME.getEnglishName(), Tag.FILM_MUSIC.getEnglishName());

        CrewDto crew1 = CrewDto.builder().id(1L).name("일요일마다 언어 교환할 분들 구함 :)").maxNum(5).currentNum(2).korLevel(1).engLevel(1).autoApproval(true).coverImage("hello").createdAt(createdAt).tags(tags).build();

        Page<CrewDto> crews = new PageImpl<>(Collections.nCopies(30, crew1), pageable, 30);

        return DataResponseDto.of(crews);
    }

    @GetMapping(value = "/crews/me")
    public DataResponseDto<Map<String, List<CrewDto>>> getMyCrews() {
        CrewDto crew1 = CrewDto.builder().id(1L).name("일요일마다 언어 교환할 분들 구함 :)").maxNum(5).currentNum(2).coverImage("hello").build();

        return DataResponseDto.of(Map.of("crews", Collections.nCopies(5, crew1)));
    }

    @GetMapping(value = "/crews/me/can-join")
    public DataResponseDto<Map<String, List<CrewDto>>> getCanJoinCrews() {
        CrewDto crew1 = CrewDto.builder().id(1L).name("일요일마다 언어 교환할 분들 구함 :)").maxNum(5).currentNum(2).coverImage("hello").build();

        return DataResponseDto.of(Map.of("crews", Collections.nCopies(5, crew1)));
    }

    @GetMapping(value = "/crews/{crewId}/voice-room")
    public DataResponseDto<VoiceRoomDto> getVoiceRoomToken() {
        long now = (new Date()).getTime();
        return DataResponseDto.of(VoiceRoomDto.builder().channelName("crew-1").token(agoraService.create(1L, 1L).getToken()).expiresIn(new Date(now + 3600).getTime()).build());
    }

    @GetMapping(value = "/crews/{crewId}/waiting-list")
    public DataResponseDto<Map<String, List<MemberDto>>> getWaitingList() {
        MemberDto member1 = MemberDto.builder().id(1L).nickname("감자").description("좋은 크루 ^^ 좋은 사람들과 ^^").application("열심히 하겠습니다람쥐").profileImage("cat").countryCode("KR").korLevel(5).engLevel(3).build();

        return DataResponseDto.of(Map.of("members", Collections.nCopies(5, member1)));
    }

    @GetMapping(value = "/crews/{crewId}/schedules/upcoming")
    public DataResponseDto<ScheduleDto> getUpcomingSchedule() {
        return DataResponseDto.of(ScheduleDto.builder().id(1L).title("OT - 자기소개(Self introduction)").dday(5).meetingTime("2023-08-01 19:00:00").profiles(List.of("cat", "dog", "cat", "dog")).build());
    }

    @GetMapping(value = "/crews/{crewId}/schedules")
    public DataResponseDto<List<ScheduleDto>> getSchedules() {
        ScheduleDto schedule1 = makeSchedule(1L, "2023-08-01 19:00:00");
        ScheduleDto schedule2 = makeSchedule(2L, "2023-08-03 19:00:00");
        ScheduleDto schedule3 = makeSchedule(3L, "2023-08-05 19:00:00");
        ScheduleDto schedule4 = makeSchedule(4L, "2023-08-11 19:00:00");
        ScheduleDto schedule5 = makeSchedule(5L, "2023-08-22 19:00:00");

        return DataResponseDto.of(List.of(schedule1, schedule2, schedule3, schedule4, schedule5));
    }

    @GetMapping(value = "/crews/{crewId}/reports/{reportId}")
    public DataResponseDto<ReportDto> getReport() {
        MemberDto member1 = MemberDto.builder().id(1L).nickname("감자").profileImage("cat").build();
        MemberDto member2 = MemberDto.builder().id(2L).nickname("고구마").profileImage("dog").build();
        MemberDto member3 = MemberDto.builder().id(3L).nickname("튀김").profileImage("lion").build();
        MemberDto member4 = MemberDto.builder().id(4L).nickname("햄버거").profileImage("pig").build();
        MemberDto member5 = MemberDto.builder().id(5L).nickname("옥수수").profileImage("bear").build();

        MemberDto feedback1 = MemberDto.builder().nickname("감자").profileImage("cat").comment("폼미칫따이").build();

        ReportDto.Keyword keyword1 = ReportDto.Keyword.builder().name("프론트엔드").count(3).build();

        return DataResponseDto.of(
            ReportDto.builder()
                .info(Info.builder().createdAt("2023-08-01 19:00:00").durationTime("00:23:40").build())
                .members(List.of(member1, member2, member3, member4, member5))
                .bubbleChart("https://user-images.githubusercontent.com/4070505/36446619-834c38b0-1647-11e8-976d-9930b8a3835b.png")
                .keyword(Collections.nCopies(4, keyword1))
                .languageRatio(LanguageRatio.builder().korean(60.0).english(40.0).build())
                .feedbacks(Collections.nCopies(4, feedback1))
                .build()
        );
    }

    @GetMapping(value = "/crews/{crewId}/reports")
    public DataResponseDto<Map<String, List<ReportDto>>> getReports() {
        MemberDto member1 = MemberDto.builder().id(1L).nickname("감자").profileImage("cat").build();
        MemberDto member2 = MemberDto.builder().id(2L).nickname("고구마").profileImage("dog").build();
        MemberDto member3 = MemberDto.builder().id(3L).nickname("튀김").profileImage("lion").build();
        MemberDto member4 = MemberDto.builder().id(4L).nickname("햄버거").profileImage("pig").build();
        MemberDto member5 = MemberDto.builder().id(5L).nickname("옥수수").profileImage("bear").build();

        ReportDto report1 = ReportDto.builder()
            .id(1L)
            .members(List.of(member1, member2, member3, member4, member5))
            .info(Info.builder().createdAt("2023-08-01 19:00:00").durationTime("00:23:40").build())
            .build();

        return DataResponseDto.of(Map.of("reports", Collections.nCopies(5, report1)));
    }

    @DeleteMapping(value = "/members/withdrawal")
    public DataResponseDto<Map<String, String>> withdrawal() {
        return DataResponseDto.of(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }

    @PostMapping(value = "/crews")
    public DataResponseDto<Map<String, Long>> create(@RequestBody CrewDto crewDto) {
        return DataResponseDto.of(Map.of("crewId", 1L));
    }

    @PostMapping(value = "/crews/{crewId}/join")
    public DataResponseDto<Map<String, String>> join(@RequestBody JoinDto joinDto) {
        return DataResponseDto.of(Map.of("message", "크루 가입이 완료되었습니다."));
    }

    @PostMapping(value = "/crews/{crewId}/accuse")
    public DataResponseDto<Map<String, String>> accuse(@RequestBody AccuseDto accuseDto) {
        return DataResponseDto.of(Map.of("message", "신고가 완료되었습니다."));
    }

    @PostMapping(value = "/crews/{crewId}/waiting-list/{memberId}")
    public DataResponseDto<Map<String, String>> accept(@RequestBody StatusDto statusDto) {
        return DataResponseDto.of(Map.of("message", "승인이 완료되었습니다."));
    }

    @DeleteMapping(value = "/crews/{crewId}/force-withdrawal/{memberId}")
    public DataResponseDto<Map<String, String>> forceWithdrawal() {
        return DataResponseDto.of(Map.of("message", "강제 탈퇴가 완료되었습니다."));
    }

    @PostMapping(value = "/crews/{crewId}/schedules")
    public DataResponseDto<Map<String, Long>> createSchedule(@RequestBody ScheduleDto scheduleDto) {
        return DataResponseDto.of(Map.of("scheduleId", 1L));
    }

    @PostMapping(value = "/crews/{crewId}/schedules/join")
    public DataResponseDto<Map<String, String>> joinSchedule(@RequestBody ScheduleDto scheduleDto) {
        return DataResponseDto.of(Map.of("message", "일정 참여가 완료되었습니다."));
    }

    @DeleteMapping(value = "/crews/{crewId}/withdrawal")
    public DataResponseDto<Map<String, String>> crewWithdrawal() {
        return DataResponseDto.of(Map.of("message", "크루 탈퇴가 완료되었습니다."));
    }

    @PatchMapping(value ="/profile")
    public DataResponseDto<Map<String, String>> updateProfile(@RequestBody ProfileDto profileDto) {
        return DataResponseDto.of(Map.of("message", "프로필 수정이 완료되었습니다."));
    }
    
    private ScheduleDto makeSchedule(Long id, String meetingTime) {
        MemberDto member1 = MemberDto.builder().id(1L).nickname("도도").profileImage("cat").build();

        return ScheduleDto.builder().id(id).title("모여라").dday(5).meetingTime(meetingTime).members(Collections.nCopies(4, member1)).build();
    }
}
