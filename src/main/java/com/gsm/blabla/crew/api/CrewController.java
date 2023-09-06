package com.gsm.blabla.crew.api;

import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.crew.dto.*;
import com.gsm.blabla.global.response.DataResponseDto;
import com.gsm.blabla.member.dto.MemberProfileResponseDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Crew 관련 API")
@RestController
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;

    @Operation(summary = "크루 생성 API")
    @PostMapping(value = "/crews")
    public DataResponseDto<Map<String, Long>> create(
        @RequestBody CrewRequestDto crewRequestDto) {
        return DataResponseDto.of(crewService.create(crewRequestDto));
    }

    @Operation(summary = "크루 단일 조회 API")
    @GetMapping(value = "/{language}/crews/{crewId}")
    public DataResponseDto<CrewResponseDto> get(
        @PathVariable("language") String language,
        @PathVariable("crewId") Long crewId) {
        return DataResponseDto.of(crewService.get(language, crewId));
    }

    // TODO: page > lastCrewId로 수정
    @Operation(summary = "크루 목록 조회 API")
    @GetMapping(value = "/{language}/crews")
    public DataResponseDto<Page<CrewResponseDto>> getAll(
        @PathVariable("language") String language,
        @PageableDefault(sort = "id", direction = Direction.DESC)  Pageable pageable) {
        return DataResponseDto.of(crewService.getAll(language, pageable));
    }

    @Operation(summary = "나의 크루 조회 API")
    @GetMapping(value = "/crews/me")
    public DataResponseDto<Map<String, List<CrewResponseDto>>> getMyCrews() {
        return DataResponseDto.of(crewService.getMyCrews());
    }

    @Operation(summary = "지금 참여 가능한 크루 조회 API")
    @GetMapping(value = "/crews/can-join")
    public DataResponseDto<Map<String, List<CrewResponseDto>>> getCanJoinCrews() {
        return DataResponseDto.of(crewService.getCanJoinCrews());
    }

    @Operation(summary = "크루 가입 신청 API")
    @PostMapping(value = "/crews/{crewId}/join")
    public DataResponseDto<Map<String, String>> joinCrew(
        @PathVariable("crewId") Long crewId,
        @RequestBody(required = false) MessageRequestDto messageRequestDto) {
        return DataResponseDto.of(crewService.joinCrew(crewId, messageRequestDto));
    }


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

    @Operation(summary = "크루 리포트 목록 조회 API")
    @GetMapping(value = "/crews/{crewId}/reports")
    public DataResponseDto<Map<String, List<CrewReportResponseDto>>> getAllReports(
        @PathVariable("crewId") Long crewId,
        @RequestParam(value = "sort", defaultValue = "desc") String sort) {
        return DataResponseDto.of(crewService.getAllReports(crewId, sort));
    }

    @Operation(summary = "크루 가입 승인 대기 인원 조회 API")
    @GetMapping(value = "/crews/{crewId}/waiting-list")
    public DataResponseDto<Map<String, List<MemberResponseDto>>> getWaitingList(@PathVariable("crewId") Long crewId) {
        return DataResponseDto.of(crewService.getWaitingList(crewId));
    }

    @Operation(summary = "크루 가입 승인 및 거절 API")
    @PostMapping(value = "/crews/{crewId}/waiting-list/{memberId}")
    public DataResponseDto<Map<String, String>> acceptOrReject(
        @PathVariable("crewId") Long crewId,
        @PathVariable("memberId") Long memberId,
        @RequestBody StatusRequestDto statusRequestDto) {
        return DataResponseDto.of(crewService.acceptOrReject(crewId, memberId, statusRequestDto));
    }

    @Operation(summary = "크루 신고하기 API")
    @PostMapping(value = "/crews/{crewId}/accuse")
    public DataResponseDto<Map<String, String>> accuse(
        @PathVariable("crewId") Long crewId,
        @RequestBody AccuseRequestDto accuseRequestDto) {
        return DataResponseDto.of(crewService.accuse(crewId, accuseRequestDto));

    }

    @Operation(summary = "크루 탈퇴 API")
    @DeleteMapping(value = "/crews/{crewId}/withdrawal")
    public DataResponseDto<Map<String, String>> withdrawal(@PathVariable Long crewId) {
        return DataResponseDto.of(crewService.withdrawal(crewId));
    }

    @Operation(summary = "크루 강제 탈퇴 API")
    @PostMapping(value = "/crews/{crewId}/force-withdrawal/{memberId}")
    public DataResponseDto<Map<String, String>> forceWithdrawal(
        @PathVariable("crewId") Long crewId,
        @PathVariable("memberId") Long crewMemberId) {
        return DataResponseDto.of(crewService.forceWithdrawal(crewId, crewMemberId));
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
