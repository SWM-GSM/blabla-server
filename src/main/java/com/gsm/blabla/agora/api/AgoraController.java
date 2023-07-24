package com.gsm.blabla.agora.api;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgoraController {
    private final AgoraService agoraService;

    @Operation(summary = "채널 생성 API")
    @GetMapping("/crews/{crewId}/voice-room")
    public DataResponseDto<RtcTokenDto> create(@PathVariable Long crewId) {
        Long memberId = SecurityUtil.getMemberId();
        return DataResponseDto.of(agoraService.create(crewId, memberId));
    }

}
