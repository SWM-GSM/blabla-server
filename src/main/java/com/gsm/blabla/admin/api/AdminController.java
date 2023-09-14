package com.gsm.blabla.admin.api;

import com.gsm.blabla.admin.application.FcmService;
import com.gsm.blabla.admin.dto.PushMessageRequestDto;
import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin 관련 API")
@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final FcmService fcmService;

    @Operation(summary = "health check API")
    @GetMapping("/health-check")
    public DataResponseDto<String> getHealthCheck() {
        return DataResponseDto.of("OK");
    }

    @Operation(summary = "FCM 푸시 알림 전송 API")
    @PostMapping(value = "/fcm")
    public DataResponseDto<Map<String, String>> pushMessage(@RequestBody PushMessageRequestDto pushMessageRequestDto) throws IOException {
        return DataResponseDto.of(fcmService.sendMessageTo(pushMessageRequestDto));
    }
}
