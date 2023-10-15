package com.gsm.blabla.member.api;

import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.application.MemberService;
import com.gsm.blabla.member.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "멤버 관련 API")
@RestController
@RequestMapping("/api/v1")
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
    public DataResponseDto<Map<String, String>> updateProfile(@Valid @RequestBody MemberProfileRequestDto memberProfileRequestDto) {
        return DataResponseDto.of(memberService.updateProfile(memberProfileRequestDto));
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
    @GetMapping("/profile")
    public DataResponseDto<MemberProfileResponseDto> getProfile(
            @Pattern(regexp = "^(ko|en)$", message = "언어는 ko 또는 en 중 하나여야 합니다.")
            @RequestHeader(name="Content-Language") String language) {
        return DataResponseDto.of(memberService.getProfile(language));
    }

    @Operation(summary = "푸쉬 알림 설정 API")
    @PatchMapping("/members/push-notification")
    public DataResponseDto<Map<String, String>> updatePushNotification(@RequestBody PushNotificationRequestDto pushNotificationRequestDto) {
        return DataResponseDto.of(memberService.updatePushNotification(pushNotificationRequestDto));
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
