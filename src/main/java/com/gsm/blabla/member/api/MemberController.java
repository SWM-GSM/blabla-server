package com.gsm.blabla.member.api;

import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.application.MemberService;
import com.gsm.blabla.member.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 조회 API")
    @GetMapping("/members/nicknames/{nickname}")
    public DataResponseDto<Map<String, Boolean>> duplicatedNickname(@PathVariable String nickname) {
        return DataResponseDto.of(memberService.isNicknameDuplicated(nickname));
    }

    @Operation(summary = "프로필 수정 API")
    @PatchMapping("/profile")
    public DataResponseDto<Map<String, String>> updateProfile(@RequestBody MemberProfileRequestDto memberProfileRequestDto) {
        return DataResponseDto.of(memberService.updateProfile(memberProfileRequestDto));
    }

    @Operation(summary = "프로필 자기소개 수정 API")
    @PatchMapping("/profile/description")
    public DataResponseDto<Map<String, String>> updateDescription(@RequestBody DescriptionRequestDto descriptionRequestDto) {
        return DataResponseDto.of(memberService.updateDescription(descriptionRequestDto));
    }

    @Operation(summary = "프로필 관심사 수정 API")
    @PatchMapping("/profile/keywords")
    public DataResponseDto<Map<String, String>> updateKeywords(@RequestBody KeywordsRequestDto keywordsRequestDto) {
        return DataResponseDto.of(memberService.updateKeywords(keywordsRequestDto));
    }

    @Operation(summary = "설정값 조회 API")
    @GetMapping("/profile/settings")
    public DataResponseDto<Map<String, Map<String, Boolean>>> getSettings() {
        return DataResponseDto.of(memberService.getSettings());
    }

    @Operation(summary = "멤버 삭제 API")
    @DeleteMapping("/members/withdrawal")
    public DataResponseDto<Map<String, String>> withdrawal() {
        return DataResponseDto.of(memberService.withdrawal());
    }

    @Operation(summary = "멤버 프로필 조회 API")
    @GetMapping("/{language}/profile")
    public DataResponseDto<MemberProfileResponseDto> getProfile(@PathVariable String language) {
        return DataResponseDto.of(memberService.getProfile(language));
    }

    @Operation(summary = "푸쉬 알림 설정 API")
    @PatchMapping("/members/push-notification")
    public DataResponseDto<Map<String, String>> updatePushNotification(@RequestBody PushNotificationRequestDto pushNotificationRequestDto) {
        return DataResponseDto.of(memberService.updatePushNotification(pushNotificationRequestDto));
    }

    @Operation(summary = "생년월일 공개 여부 설정 API")
    @PatchMapping("/members/birth-date-disclosure")
    public DataResponseDto<Map<String, String>> updateBirthDateDisclosure(@RequestBody BirthDateDisclosureRequestDto birthDateDisclosureRequestDto) {
        return DataResponseDto.of(memberService.updateBirthDateDisclosure(birthDateDisclosureRequestDto));
    }

    @Operation(summary = "성별 공개 여부 설정 API")
    @PatchMapping("/members/gender-disclosure")
    public DataResponseDto<Map<String, String>> updateGenderDisclosure(@RequestBody genderDisclosureRequestDto genderDisclosureRequestDto) {
        return DataResponseDto.of(memberService.updateGenderDisclosure(genderDisclosureRequestDto));
    }

    @Operation(summary = "나의 memberId 조회 API")
    @GetMapping("/members/my-id")
    public DataResponseDto<Map<String, Long>> getMyId() {
        return DataResponseDto.of(memberService.getMyId());
    }

    @Operation(summary = "memberId로 프로필 리스트 조회 API")
    @PostMapping("/members/id-to-info")
    public DataResponseDto<Map<String, List<MemberResponseDto>>> getInfosFromIds(@RequestBody MemberRequestDto memberRequestDto) {
        return DataResponseDto.of(memberService.getInfosFromIds(memberRequestDto));
    }
}
