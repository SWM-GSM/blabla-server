package com.gsm.blabla.member.api;

import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.application.MemberService;
import com.gsm.blabla.member.dto.MemberRequestDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public DataResponseDto<Map<String, String>> updateProfile(@RequestBody MemberRequestDto memberRequestDto) {
        return DataResponseDto.of(memberService.updateProfile(memberRequestDto));
    }

    @Operation(summary = "멤버 삭제 API")
    @DeleteMapping("/members/withdrawal")
    public DataResponseDto<Map<String, String>> withdrawal() {
        return DataResponseDto.of(memberService.withdrawal());
    }
}
