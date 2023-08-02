package com.gsm.blabla.dummy.api;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.member.dto.*;
import com.gsm.blabla.practice.domain.Content;
import com.gsm.blabla.practice.domain.MemberContent;
import com.gsm.blabla.practice.dto.*;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.dummy.dto.AccuseDto;
import com.gsm.blabla.dummy.dto.CrewDto;
import com.gsm.blabla.dummy.dto.JoinDto;
import com.gsm.blabla.dummy.dto.MemberDto;
import com.gsm.blabla.dummy.dto.ProfileDto;
import com.gsm.blabla.dummy.dto.ReportDto;
import com.gsm.blabla.dummy.dto.ReportDto.Info;
import com.gsm.blabla.dummy.dto.ReportDto.LanguageRatio;
import com.gsm.blabla.dummy.dto.ScheduleDto;
import com.gsm.blabla.dummy.dto.StatusDto;
import com.gsm.blabla.dummy.dto.VoiceRoomDto;
import com.gsm.blabla.global.response.DataResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.gsm.blabla.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dummy")
public class DummyController {

    private final AgoraService agoraService;

    @GetMapping(value = {"/{language}/profile", "{language}/crews/{crewId}/profile/{memberId}"})
    public DataResponseDto<MemberProfileResponseDto> getProfile(@PathVariable String language) {
        List<Keyword> keywords = new ArrayList<>();
        keywords.add(Keyword.GAME);
        keywords.add(Keyword.NETFLIX);

        List<Map<String, String>> interests = keywords.stream()
                .map(keyword -> {
                    Map<String, String> interest = new HashMap<>();
                    if ("ko".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getKoreanName());
                        interest.put("tag", keyword.name());
                    } else if ("en".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getEnglishName());
                        interest.put("tag", keyword.name());
                    }
                    return interest;
                })
                .toList();
        Member member = Member.builder()
                .nickname("가나다라마바사")
                .profileImage("cat")
                .countryCode("KR")
                .korLevel(5)
                .engLevel(3)
                .gender("female")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();

        MemberProfileResponseDto memberProfileResponseDto = MemberProfileResponseDto.builder()
                .nickname(member.getNickname())
                .description(member.getDescription())
                .profileImage(member.getProfileImage())
                .countryCode(member.getCountryCode())
                .birthDate(member.getBirthDate())
                .korLevel(member.getKorLevel())
                .engLevel(member.getEngLevel())
                .isLeader(true)
                .gender(member.getGender())
                .signedUpAfter(1L)
                .keywords(interests)
                .build();

        return DataResponseDto.of(memberProfileResponseDto);
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
        CrewDto crew2 = CrewDto.builder().id(2L).name("감자네 언어교환").maxNum(5).currentNum(2).coverImage("book").build();
        CrewDto crew3 = CrewDto.builder().id(3L).name("고구마네 언어교환").maxNum(5).currentNum(2).coverImage("cooking").build();
        CrewDto crew4 = CrewDto.builder().id(4L).name("옥수수네 언어교환").maxNum(5).currentNum(2).coverImage("exercise").build();


        return DataResponseDto.of(Map.of("crews", List.of(crew1, crew2, crew3, crew4)));
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

    @GetMapping("/contents/{contentId}")
    public DataResponseDto<ContentResponseDto> get(@PathVariable Long contentId) {
        ContentResponseDto contentResponseDto = ContentResponseDto.builder()
                .contentUrl("https://www.youtube.com/watch?v=9mQk7Evt6Vs")
                .sentence("지금 만나자!")
                .answer("그때 만나자!")
                .topic("시간 약속 정하기")
                .title("애니메이션 - 아이스베어")
                .level(1L)
                .build();
        return DataResponseDto.of(contentResponseDto);
    }

    @GetMapping("/{language}/contents")
    public DataResponseDto<Map<String, ContentListResponseDto>> getAll(@PathVariable String language) {
            Map<String, ContentListResponseDto> result = new HashMap<>();
            List<ContentViewResponseDto> contents = new ArrayList<>();
            ContentViewResponseDto contentViewResponseDto = ContentViewResponseDto.builder()
                    .id(1L)
                    .level(1L)
                    .topic("Topic 2")
                    .title("Title 2")
                    .thumbnailUrl("https://img.youtube.com/vi/BUic6FWvRDg/hqdefault.jpg")
                    .isCompleted(true)
                    .build();
            contents.add(contentViewResponseDto);
            contents.add(contentViewResponseDto);
            contents.add(contentViewResponseDto);
            contents.add(contentViewResponseDto);
            contents.add(contentViewResponseDto);
            contents.add(contentViewResponseDto);
            ContentListResponseDto contentListResponseDto = ContentListResponseDto.builder()
                    .progress(100.0)
                    .contents(contents)
                    .build();
        result.put("level1", contentListResponseDto);
        result.put("level2", contentListResponseDto);
        result.put("level3", contentListResponseDto);
        result.put("level4", contentListResponseDto);
        result.put("level5", contentListResponseDto);

        return DataResponseDto.of(result);
    }

    @GetMapping("/{language}/contents/today")
    public DataResponseDto<ContentViewResponseDto> getTodayContent(@PathVariable String language) {
        ContentViewResponseDto contentViewResponseDto = ContentViewResponseDto.builder()
                .id(1L)
                .level(1L)
                .topic("Topic 2")
                .title("Title 2")
                .thumbnailUrl("https://img.youtube.com/vi/BUic6FWvRDg/hqdefault.jpg")
                .isCompleted(true)
                .build();
        return DataResponseDto.of(contentViewResponseDto);
    }

    @GetMapping("/contents/{contentId}/feedback")
    public DataResponseDto<PracticeFeedbackResponseDto> getFeedback(
            @PathVariable Long contentId) {

        Member member = Member.builder()
                .nickname("감자")
                .profileImage("cat")
                .build();

        Content content = Content.builder()
                .id(contentId)
                .level(1L)
                .topic("Topic 2")
                .title("Title 2")
                .build();

        MemberContent memberContent = MemberContent.builder()
                .member(member)
                .content(content)
                .userAnswer("거의 다 왔어")
                .shortFeedback(null)
                .longFeedback("In Korean, both \\\"거의 다 왔어\\\" and \\\"거의 다 했어\\\" convey a similar meaning, which is \\\"I'm almost there\\\" or \\\"I'm almost done.\\\" However, there is a subtle difference in their usage.\\n\\n\\\"거의 다 왔어\\\" is used when referring to a physical location or a destination. It implies that you are almost at the place you are going to. For example, if you are meeting someone at a cafe and you are close to arriving, you would say \\\"거의 다 왔어\\\" to indicate that you are almost there.\\n\\nOn the other hand, \\\"거의 다 했어\\\" is used when talking about completing an action or task. It implies that you are almost finished doing something. For example, if you are almost done with your homework, you would say \\\"거의 다 했어\\\" to express that you are almost finished.\\n\\nIn this case, the correct answer is \\\"거의 다 왔어\\\" because the learner is referring to a physical location or a destination. They are saying that they are almost at the place, not that they are almost done with something.")
                .starScore(0.78)
                .contextScore(0.5)
                .build();

        return DataResponseDto.of(PracticeFeedbackResponseDto.of(memberContent));
    }


    @PostMapping("/contents/{contentId}/feedback")
    public DataResponseDto<PracticeFeedbackResponseDto> feedback(
            @PathVariable Long contentId,
            @RequestBody UserAnswerRequestDto userAnswerRequestDto) {

        Member member = Member.builder()
                .nickname("감자")
                .profileImage("cat")
                .build();

        Content content = Content.builder()
                .id(contentId)
                .level(1L)
                .topic("Topic 2")
                .title("Title 2")
                .build();

        MemberContent memberContent = MemberContent.builder()
                .member(member)
                .content(content)
                .userAnswer(userAnswerRequestDto.getUserAnswer())
                .shortFeedback(null)
                .longFeedback("In Korean, both \\\"거의 다 왔어\\\" and \\\"거의 다 했어\\\" convey a similar meaning, which is \\\"I'm almost there\\\" or \\\"I'm almost done.\\\" However, there is a subtle difference in their usage.\\n\\n\\\"거의 다 왔어\\\" is used when referring to a physical location or a destination. It implies that you are almost at the place you are going to. For example, if you are meeting someone at a cafe and you are close to arriving, you would say \\\"거의 다 왔어\\\" to indicate that you are almost there.\\n\\nOn the other hand, \\\"거의 다 했어\\\" is used when talking about completing an action or task. It implies that you are almost finished doing something. For example, if you are almost done with your homework, you would say \\\"거의 다 했어\\\" to express that you are almost finished.\\n\\nIn this case, the correct answer is \\\"거의 다 왔어\\\" because the learner is referring to a physical location or a destination. They are saying that they are almost at the place, not that they are almost done with something.")
                .starScore(0.78)
                .contextScore(0.5)
                .build();

        return DataResponseDto.of(PracticeFeedbackResponseDto.of(memberContent));
    }

    @PatchMapping("/members/push-notification")
    public DataResponseDto<Map<String, String>> updatePushNotification(@RequestBody PushNotificationRequestDto pushNotificationRequestDto) {
        return DataResponseDto.of(Map.of("message", "푸쉬 알림 설정이 완료되었습니다."));
    }

    @PatchMapping("/members/birth-date-disclosure")
    public DataResponseDto<Map<String, String>> updateBirthDateDisclosure(@RequestBody BirthDateDisclosureRequestDto birthDateDisclosureRequestDto) {
        return DataResponseDto.of(Map.of("message", "생년월일 공개 여부 설정이 완료되었습니다."));
    }

    @PatchMapping("/members/gender-disclosure")
    public DataResponseDto<Map<String, String>> updateGenderDisclosure(@RequestBody genderDisclosureRequestDto genderDisclosureRequestDto) {
        return DataResponseDto.of(Map.of("message", "성별 공개 여부 설정이 완료되었습니다."));
    }

    @PatchMapping("/profile/description")
    public DataResponseDto<Map<String, String>> updateDescription(@RequestBody DescriptionRequestDto descriptionRequestDto) {
        return DataResponseDto.of(Map.of("message", "프로필 자기소개 수정이 완료되었습니다."));
    }

    @PatchMapping("/profile/keywords")
    public DataResponseDto<Map<String, String>> updateKeywords(@RequestBody KeywordsRequestDto keywordsRequestDto) {
        return DataResponseDto.of(Map.of("message", "프로필 관심사 수정이 완료되었습니다."));
    }

    @PostMapping("/contents/{contentId}/practice")
    public DataResponseDto<Map<String, String>> createPracticeHistory(
            @PathVariable Long contentId,
            @RequestParam("files") List<MultipartFile> files) {
        return DataResponseDto.of(Map.of("message", "연습 기록 음성 파일 저장이 완료되었습니다."));
    }

    @PostMapping(value = "/reports/{reportId}")
    public DataResponseDto<Map<String, String>> uploadAndAnalyzeVoiceFile(
            @PathVariable("reportId") Long reportId,
            @RequestParam("file") MultipartFile file) {
        return DataResponseDto.of(Map.of("message", "음성 파일 분석이 완료되었습니다."));
    }

    @GetMapping(value = "/reports/{reportId}")
    public DataResponseDto<Map<String, String>> createReport(
            @PathVariable("reportId") Long reportId) {
        return DataResponseDto.of(Map.of("message", "리포트 생성이 완료되었습니다."));
    }
}
