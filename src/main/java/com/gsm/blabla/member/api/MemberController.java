package com.gsm.blabla.member.api;

import com.gsm.blabla.global.common.dto.DataResponseDto;
import com.gsm.blabla.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/nicknames/{nickname}")
    public DataResponseDto<String> duplicatedNickname(@PathVariable String nickname) {
        if (memberService.isNicknameDuplicate(nickname)) {
            return DataResponseDto.of("중복된 닉네임입니다.");
        }
        return DataResponseDto.of("사용 가능한 닉네임입니다.");
    }
}
