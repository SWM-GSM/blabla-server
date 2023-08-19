package com.gsm.blabla.fcm.api;

import com.gsm.blabla.fcm.application.FcmService;
import com.gsm.blabla.fcm.dto.PushMessageRequestDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FCM 관련 API")
@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @Operation(summary = "FCM 푸시 알림 전송 API")
    @PostMapping(value = "/fcm")
    public DataResponseDto<String> pushMessage(@RequestBody PushMessageRequestDto pushMessageRequestDto) throws IOException {
        return DataResponseDto.of(fcmService.sendMessageTo(pushMessageRequestDto));
    }
}
