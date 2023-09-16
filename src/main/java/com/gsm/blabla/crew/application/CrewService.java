package com.gsm.blabla.crew.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.admin.application.FcmService;
import com.gsm.blabla.admin.dto.FcmMessageRequestDto;
import com.gsm.blabla.crew.dao.*;
import com.gsm.blabla.crew.domain.*;
import com.gsm.blabla.crew.dto.*;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberProfileResponseDto;
import com.gsm.blabla.member.dto.MemberResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CrewService {

    private final MemberRepository memberRepository;
    private final VoiceFileRepository voiceFileRepository;
    private final CrewReportRepository crewReportRepository;
    private final S3UploaderService s3UploaderService;
    private final FcmService fcmService;
    private final RestTemplate restTemplate;
    private final CrewReportAnalysisRepository crewReportAnalysisRepository;
    private final CrewReportKeywordRepository crewReportKeywordRepository;

    @Value("${ai.voice-analysis-url}")
    private String voiceAnalysisUrl;

    @Value("${ai.report-analysis-trigger-url}")
    private String reportAnalysisTriggerUrl;

    @Value("${ai.report-analysis-url}")
    private String reportAnalysisUrl;



    public Map<String, String> uploadAndAnalyzeVoiceFile(Long reportId, MultipartFile file) {
        if (file.getSize() == 0) {
            throw new GeneralException(Code.FILE_IS_EMPTY, "음성 파일이 비어있습니다.");
        }

        Long memberId = SecurityUtil.getMemberId();

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm"));
        String filePath = String.format("reports/%s/members/%s/%s/%s.wav", String.valueOf(reportId), String.valueOf(memberId), fileName, fileName);
        String fileUrl = s3UploaderService.uploadFile(filePath, file);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonFileUrl = "{\"fileUrl\":\"" + fileUrl + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFileUrl, headers);
        String response = restTemplate.postForObject(voiceAnalysisUrl, requestEntity, String.class);

        VoiceAnalysisResponseDto voiceAnalysisResponseDto = null;
        try {
            voiceAnalysisResponseDto = objectMapper.readValue(response, VoiceAnalysisResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (voiceAnalysisResponseDto == null) {
            throw new GeneralException(Code.VOICE_ANALYSIS_IS_NULL, "음성 분석 결과가 비어있습니다.");
        }

        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
                () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        voiceFileRepository.save(
                VoiceFile.builder()
                        .member(member)
                        .crewReport(crewReport)
                        .fileUrl(fileUrl)
                        .totalCallTime(voiceAnalysisResponseDto.getTotalCallTime())
                        .koreanTime(voiceAnalysisResponseDto.getKoreanTime())
                        .englishTime(voiceAnalysisResponseDto.getEnglishTime())
                        .redundancyTime(voiceAnalysisResponseDto.getRedundancyTime())
                        .build()
        );
        return Collections.singletonMap("message", "음성 파일 분석이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public MemberProfileResponseDto getMemberProfile(String language, Long crewId, Long memberId) {
        if (!Objects.equals(language, "ko") && !Objects.equals(language, "en")) {
            throw new GeneralException(Code.LANGUAGE_NOT_SUPPORTED, "ko 또는 en만 지원합니다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        return MemberProfileResponseDto.getCrewMemberProfile(member);
    }

    public Map<String, String> createReportRequest(Long reportId, TargetTokenDto targetTokenDto) {

        LocalDateTime endAt = LocalDateTime.now();
        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
                () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );
        crewReport.updateEndAt(endAt);

        Long voiceFileCount = voiceFileRepository.countAllByCrewReportId(reportId);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("reportId", String.valueOf(reportId));
        paramMap.put("targetVoiceFileCount", String.valueOf(voiceFileCount));
        paramMap.put("targetToken", targetTokenDto.getTargetToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(paramMap, headers);

        // TODO: response 예외 처리
        restTemplate.postForObject(reportAnalysisTriggerUrl, requestEntity, String.class);

        return Collections.singletonMap("message", "리포트 생성 요청이 완료되었습니다.");
    }

    public Map<String, String> createReport(Long reportId, TargetTokenDto targetTokenDto) {

        List<VoiceFile> voiceFiles = voiceFileRepository.getAllByCrewReportId(reportId);
        List<String> fileUrls = voiceFiles.stream()
                .map(VoiceFile::getFileUrl)
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(fileUrls, headers);
        String response = restTemplate.postForObject(reportAnalysisUrl, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        AiCrewReportResponseDto aiCrewReportResponseDto = null;
        try {
            aiCrewReportResponseDto = objectMapper.readValue(response, AiCrewReportResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (aiCrewReportResponseDto == null) {
            throw new GeneralException(Code.REPORT_ANALYSIS_IS_NULL, "리포트 분석 결과가 비어있습니다.");
        }

        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
                () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );

        crewReportAnalysisRepository.save(
                CrewReportAnalysis.builder()
                        .crewReport(crewReport)
                        .koreanTime(aiCrewReportResponseDto.getKoreanTime())
                        .englishTime(aiCrewReportResponseDto.getEnglishTime())
                        .cloudUrl(aiCrewReportResponseDto.getCloudUrl())
                        .build()
                );

        crewReportKeywordRepository.saveAll(
                aiCrewReportResponseDto.getKeywords().stream()
                        .map(crewReportKeywordDto -> CrewReportKeyword.builder()
                                .crewReport(crewReport)
                                .keyword(crewReportKeywordDto.getKeyword())
                                .count(crewReportKeywordDto.getCount())
                                .build()
                        )
                        .toList());

        FcmMessageRequestDto fcmMessageRequestDto = FcmMessageRequestDto.builder()
                .targetToken(targetTokenDto.getTargetToken())
                .title("리포트 생성 완료")
                .body("리포트 생성이 완료되었습니다.")
                .build();
        try {
            fcmService.sendMessageTo(fcmMessageRequestDto);
        } catch (IOException e) {
            throw new GeneralException(Code.FCM_FAILED, "FCM 메시지 전송에 실패했습니다.");
        }

        return Collections.singletonMap("message", "리포트 생성이 완료되었습니다.");
    }

    public Map<String, String> createFeedback(Long voiceFileId, VoiceFileFeedbackRequestDto voiceFileFeedbackRequestDto) {
        VoiceFile voiceFile = voiceFileRepository.findById(voiceFileId).orElseThrow(
                () -> new GeneralException(Code.VOICE_FILE_NOT_FOUND, "존재하지 않는 음성 파일입니다.")
        );

        voiceFile.createFeedback(voiceFileFeedbackRequestDto.getContent());

        return Collections.singletonMap("message", "음성 채팅 피드백 저장이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public CrewReportResponseDto getReport(Long reportId) {
        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
            () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );
        CrewReportAnalysis crewReportAnalysis = crewReportAnalysisRepository.findByCrewReport(crewReport).orElseThrow(
            () -> new GeneralException(Code.REPORT_ANALYSIS_IS_NULL, "존재하지 않는 리포트 분석입니다.")
        );

        // info
        Map<String, String> info = getReportInfo(crewReport, crewReportAnalysis);

        // members
        List<MemberResponseDto> members = crewReport.getVoiceFiles().stream()
                .map(VoiceFile::getMember)
                .distinct()
                .map(MemberResponseDto::crewReportResponse)
                .toList();

        // bubble chart
        String bubbleChart = crewReportAnalysis.getCloudUrl();

        // keyword
        List<Map<String, Object>> keyword = crewReport.getKeywords().stream()
                .map(crewReportKeyword -> {
                    Map<String, Object> keywordMap = new HashMap<>();
                    keywordMap.put("name", crewReportKeyword.getKeyword());
                    keywordMap.put("count", crewReportKeyword.getCount());
                    return keywordMap;
                })
                .toList();

        // language ratio
        Map<String, Integer> languageRatio = new HashMap<>();

        Duration koreanTime = crewReportAnalysis.getKoreanTime();
        Duration englishTime = crewReportAnalysis.getEnglishTime();

        long totalMilliseconds = koreanTime.toMillis() + englishTime.toMillis();

        double koreanRatio = (double) koreanTime.toMillis() / totalMilliseconds;

        int koreanPercentage = (int) (koreanRatio * 100);
        int englishPercentage = 100 - koreanPercentage;
        languageRatio.put("korean", koreanPercentage);
        languageRatio.put("english", englishPercentage);

        // feedbacks
        List<MemberResponseDto> feedbacks = crewReport.getVoiceFiles().stream()
            .map(voiceFile -> MemberResponseDto.feedbackResponse(voiceFile.getMember(), voiceFile))
            .filter(Objects::nonNull)
            .toList();

        return CrewReportResponseDto.crewReportResponse(info, members, bubbleChart, keyword, languageRatio, feedbacks);
    }

    private Map<String, String> getReportInfo(CrewReport crewReport, CrewReportAnalysis crewReportAnalysis) {
        Map<String, String> info = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        info.put("createdAt", crewReportAnalysis.getCreatedAt().format(formatter));

        String durationTime = "2000-01-01 00:00";
        if (!Objects.equals(crewReport.getEndAt(), LocalDateTime.of(2000, 1, 1, 0, 0, 0))) {
            Duration duration = Duration.between(crewReport.getStartedAt(), crewReport.getEndAt());
            durationTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        }
        info.put("durationTime", durationTime);

        return info;
    }
}
