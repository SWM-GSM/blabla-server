package com.gsm.blabla.agora.api;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.agora.dto.AccuseRequestDto;
import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Agora 관련 API")
@RestController
@RequestMapping("/crews/voice-room")
@RequiredArgsConstructor
public class AgoraController {
    private final AgoraService agoraService;

    @Operation(summary = "보이스룸 입장 API")
    @PostMapping(value = "")
    public DataResponseDto<RtcTokenDto> create(
        @RequestBody VoiceRoomRequestDto voiceRoomRequestDto) {
        return DataResponseDto.of(agoraService.create(voiceRoomRequestDto));
    }

    @Operation(summary = "보이스룸 접속 유저 목록 조회 API")
    @GetMapping(value = "")
    public DataResponseDto<Map<String, List<MemberResponseDto>>> getMembers() {
        return DataResponseDto.of(agoraService.getMembers());
    }

    @Operation(summary = "이전 보이스룸 접속 유저 목록 조회 API")
    @GetMapping(value = "/previous/{reportId}")
    public DataResponseDto<Map<String, List<MemberResponseDto>>> getPreviousMembers(
            @PathVariable Long reportId) {
        return DataResponseDto.of(agoraService.getPreviousMembers(reportId));
    }

    @Operation(summary = "보이스룸 신고 API")
    @PostMapping(value = "/accuse")
    public DataResponseDto<Map<String, String>> accuse(
        @RequestBody AccuseRequestDto accuseRequestDto
    ) {
        return DataResponseDto.of(agoraService.accuse(accuseRequestDto));
    }
}
