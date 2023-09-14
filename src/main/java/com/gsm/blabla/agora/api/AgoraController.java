package com.gsm.blabla.agora.api;

import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agora 관련 API")
@RestController
@RequiredArgsConstructor
public class AgoraController {
    private final AgoraService agoraService;

    @Operation(summary = "보이스룸 입장 API")
    @PostMapping(value = "/crews/voice-room")
    public DataResponseDto<RtcTokenDto> create(
        @RequestBody VoiceRoomRequestDto voiceRoomRequestDto) {
        return DataResponseDto.of(agoraService.create(voiceRoomRequestDto));
    }

}
