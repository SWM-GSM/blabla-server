package com.gsm.blabla.admin.api;

import com.gsm.blabla.global.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin 관련 API")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Operation(summary = "health check API")
    @GetMapping("/health-check")
    public DataResponseDto<String> getHealthCheck() {
        return DataResponseDto.of("OK");
    }
}
