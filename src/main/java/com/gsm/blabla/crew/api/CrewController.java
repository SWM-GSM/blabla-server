package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dto.*;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.dto.MemberProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Crew 관련 API")
@RestController
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;


    @Operation(summary = "음성 파일 업로드 & 분석 API")
    @PostMapping(value = "/crews/reports/{reportId}/voice-file")
    public DataResponseDto<Map<String, String>> uploadAndAnalyzeVoiceFile(
            @PathVariable("reportId") Long reportId,
            @RequestParam("file") MultipartFile file) {
        return DataResponseDto.of(crewService.uploadAndAnalyzeVoiceFile(reportId, file));
    }

    @Operation(summary = "크루 리포트 생성 API")
    @PostMapping(value = "/crews/reports/{reportId}")
    public DataResponseDto<Map<String, String>> createReport(
            @PathVariable("reportId") Long reportId) {
        return DataResponseDto.of(crewService.createReport(reportId));
    }


    @Operation(summary = "크루 리포트 생성 요청 API")
    @PostMapping(value = "/crews/reports/{reportId}/request")
    public DataResponseDto<Map<String, String>> createReportRequest(
            @PathVariable("reportId") Long reportId) {
        return DataResponseDto.of(crewService.createReportRequest(reportId));
    }

    @Operation(summary = "크루 리포트 조회 API")
    @GetMapping(value = "/crews/reports/{reportId}")
    public DataResponseDto<CrewReportResponseDto> getReport(
            @PathVariable("reportId") Long reportId) {
        return DataResponseDto.of(crewService.getReport(reportId));
    }

    @Operation(summary = "멤버 프로필 조회 API")
    @GetMapping("/{language}/crews/{crewId}/profile/{memberId}")
    public DataResponseDto<MemberProfileResponseDto> getMemberProfile(
            @PathVariable String language,
            @PathVariable Long crewId,
            @PathVariable Long memberId) {
        return DataResponseDto.of(crewService.getMemberProfile(language, crewId, memberId));
    }

    @Operation(summary = "음성 채팅 유저 피드백 저장 API")
    @PostMapping(value = "/voice-files/{voiceFileId}/feedback")
    public DataResponseDto<Map<String, String>> createFeedback(
            @PathVariable("voiceFileId") Long voiceFileId,
            @RequestBody VoiceFileFeedbackRequestDto voiceFileFeedbackRequestDto) {
        return DataResponseDto.of(crewService.createFeedback(voiceFileId, voiceFileFeedbackRequestDto));
    }
}
