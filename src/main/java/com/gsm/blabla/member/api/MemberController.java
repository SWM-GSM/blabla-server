package com.gsm.blabla.member.api;

import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 조회 API")
    @GetMapping("/nicknames/{nickname}")
    public DataResponseDto<Map<String, Boolean>> duplicatedNickname(@PathVariable String nickname) {
        return DataResponseDto.of(memberService.isNicknameDuplicated(nickname));
    }
}
