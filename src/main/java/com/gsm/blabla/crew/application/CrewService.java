package com.gsm.blabla.crew.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.crew.dao.*;
import com.gsm.blabla.crew.domain.*;
import com.gsm.blabla.crew.dto.*;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberProfileResponseDto;
import com.gsm.blabla.member.dto.MemberResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CrewRepository crewRepository;
    private final CrewTagRepository crewTagRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final ApplyMessageRepository applyMessageRepository;
    private final VoiceFileRepository voiceFileRepository;
    private final CrewReportRepository crewReportRepository;
    private final S3UploaderService s3UploaderService;
    private final RestTemplate restTemplate;
    private final CrewAccuseRepository crewAccuseRepository;
    private final CrewReportAnalysisRepository crewReportAnalysisRepository;
    private final CrewReportKeywordRepository crewReportKeywordRepository;

    public Map<String, String> uploadAndAnalyzeVoiceFile(Long reportId, MultipartFile file) {
        if (file.getSize() == 0) {
            throw new GeneralException(Code.FILE_IS_EMPTY, "음성 파일이 비어있습니다.");
        }

        Long memberId = SecurityUtil.getMemberId();

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm"));
        String filePath = String.format("reports/%s/members/%s/%s/%s.wav", String.valueOf(reportId), String.valueOf(memberId), fileName, fileName);
        String fileUrl = s3UploaderService.uploadFile(filePath, file);

        String fastApiUrl = "http://13.209.117.21:8000/ai/voice-analysis";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonFileUrl = "{\"fileUrl\":\"" + fileUrl + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFileUrl, headers);
        String response = restTemplate.postForObject(fastApiUrl, requestEntity, String.class);

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

        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_NOT_JOINED, "해당 멤버는 해당 크루의 멤버가 아닙니다."));


        return MemberProfileResponseDto.getCrewMemberProfile(member);
    }

    public Map<String, String> createReportRequest(Long reportId) {

        LocalDateTime endAt = LocalDateTime.now();
        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
                () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );
        crewReport.updateEndAt(endAt);

        Long voiceFileCount = voiceFileRepository.countAllByCrewReportId(reportId);

        String crewReportAnalysisTriggerUrl = "https://z64kktsmu3.execute-api.ap-northeast-2.amazonaws.com/dev/ai/crew-report-analysis/sqs";

        Map<String, Long> paramMap = new HashMap<>();
        paramMap.put("reportId", reportId);
        paramMap.put("targetVoiceFileCount", voiceFileCount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(paramMap, headers);

        // TODO: response 예외 처리
        restTemplate.postForObject(crewReportAnalysisTriggerUrl, requestEntity, String.class);

        return Collections.singletonMap("message", "리포트 생성 요청이 완료되었습니다.");
    }

    public Map<String, String> createReport(Long reportId) {

        List<VoiceFile> voiceFiles = voiceFileRepository.getAllByCrewReportId(reportId);
        List<String> fileUrls = voiceFiles.stream()
                .map(VoiceFile::getFileUrl)
                .toList();

        String crewReportAnalysisUrl = "https://z64kktsmu3.execute-api.ap-northeast-2.amazonaws.com/dev/ai/crew-report-analysis";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(fileUrls, headers);
        String response = restTemplate.postForObject(crewReportAnalysisUrl, requestEntity, String.class);

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

    @Transactional(readOnly = true)
    public Map<String, List<CrewReportResponseDto>> getAllReports(Long crewId, String sort) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );
        List<CrewReport> crewReports = crewReportRepository.findAll();

        List<CrewReportResponseDto> reports = new ArrayList<>(
            crewReports.stream()
            .map(crewReport -> {
                boolean generated = crewReportAnalysisRepository.findByCrewReport(crewReport)
                    .isPresent();
                List<MemberResponseDto> members = crewReport.getVoiceFiles().stream()
                    .map(VoiceFile::getMember)
                    .distinct()
                    .map(MemberResponseDto::crewReportResponse)
                    .toList();

                Optional<CrewReportAnalysis> crewReportAnalysis = crewReportAnalysisRepository.findByCrewReport(
                    crewReport);

                Map<String, String> info = crewReportAnalysis.map(
                    reportAnalysis -> getReportInfo(crewReport, reportAnalysis)).orElse(
                        Map.of("createdAt", "2000-01-01 00:00",
                            "durationTime", "2000-01-01 00:00"
                        )
                );

                return CrewReportResponseDto.crewReportListResponse(crewReport.getId(), generated,
                    members, info);
            })
            .sorted(Comparator
                .comparing(CrewReportResponseDto::getGenerated)
                .thenComparing((CrewReportResponseDto report) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(report.getInfo().get("createdAt"), formatter);
            }, Comparator.reverseOrder()))
            .toList()
        );

        if (sort.equals("asc")) {
            Collections.reverse(reports);
        }

        return Collections.singletonMap("reports", reports);
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
