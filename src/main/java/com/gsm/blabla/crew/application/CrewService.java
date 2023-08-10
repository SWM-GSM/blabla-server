package com.gsm.blabla.crew.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.crew.dao.*;
import com.gsm.blabla.crew.domain.*;
import com.gsm.blabla.crew.dto.*;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.crew.dao.ApplyMessageRepository;
import com.gsm.blabla.crew.dao.CrewAccuseRepository;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.domain.ApplyMessage;
import com.gsm.blabla.crew.domain.ApplyMessageStatus;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewAccuse;
import com.gsm.blabla.crew.domain.CrewAccuseType;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberKeywordRepository;
import com.gsm.blabla.member.dao.MemberRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.domain.MemberKeyword;
import com.gsm.blabla.member.dto.MemberProfileResponseDto;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
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
    private final MemberKeywordRepository memberKeywordRepository;
    private final CrewReportAnalysisRepository crewReportAnalysisRepository;
    private final CrewReportKeywordRepository crewReportKeywordRepository;

    public Map<String, Long> create(CrewRequestDto crewRequestDto) {
        Crew crew = crewRepository.save(crewRequestDto.toEntity());

        crewRequestDto.getTags().forEach(tag ->
                crewTagRepository.save(CrewTag.builder()
                    .crew(crew)
                    .tag(tag)
                    .build()
                )
        );

        crewMemberRepository.save(CrewMember.builder()
            .member(memberRepository.findById(SecurityUtil.getMemberId()).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
            ))
            .crew(crew)
            .role(CrewMemberRole.LEADER)
            .build()
        );

        return Collections.singletonMap("crewId", crew.getId());
    }

    @Transactional(readOnly = true)
    public CrewResponseDto get(String language, Long crewId) {
        Long memberId = SecurityUtil.getMemberId();
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );

        String status = "";
        Optional<CrewMember> crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId);
        Optional<ApplyMessage> applyMessage = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isPresent()) {
            status = crewMember.get().getStatus().toString();
        } else if (applyMessage.isPresent()) {
            status = applyMessage.get().getStatus().toString();
        } else {
            status = "NOTHING";
        }

        return CrewResponseDto.crewResponse(language, crew, status, crewMemberRepository);
    }

    @Transactional(readOnly = true)
    public Page<CrewResponseDto> getAll(String language, Pageable pageable) {
        return crewRepository.findAll(pageable).map(crew ->
            CrewResponseDto.crewListResponse(language, crew, crewMemberRepository));
    }

    public Map<String, List<CrewResponseDto>> getMyCrews() {
        Long memberId = SecurityUtil.getMemberId();
        List<CrewMember> crewMembers = crewMemberRepository.getByMemberIdAndStatus(memberId, CrewMemberStatus.JOINED);

        List<CrewResponseDto> crews = crewMembers.stream()
            .map(crewMember -> CrewResponseDto.myCrewListResponse(crewMember.getCrew(), crewMemberRepository))
            .toList();

        return Collections.singletonMap("crews", crews);
    }

    public Map<String, List<CrewResponseDto>> getCanJoinCrews() {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        List<CrewResponseDto> crews = crewRepository.findCrewsThatCanBeJoined(memberId, member.getKorLevel(), member.getEngLevel())
            .stream()
            .filter(crew -> crewMemberRepository.countCrewMembersByCrewIdAndStatus(crew.getId(), CrewMemberStatus.JOINED) < crew.getMaxNum())
            .sorted(
                Comparator.comparingInt(crew -> crew.getMaxNum() - crewMemberRepository.countCrewMembersByCrewIdAndStatus(crew.getId(), CrewMemberStatus.JOINED))
            )
            .sorted(Comparator.comparing(Crew::getAutoApproval).reversed())
            .map(crew -> CrewResponseDto.myCrewListResponse(crew, crewMemberRepository))
            .limit(10)
            .toList();

        return Collections.singletonMap("crews", crews);
    }

    public Map<String, String> joinCrew(Long crewId, MessageRequestDto messageRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );
        String message = "";

        boolean isJoined = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent();
        if (isJoined) {
            throw new GeneralException(Code.CREW_ALREADY_JOINED, "이미 가입한 크루입니다.");
        }

        boolean isApplied = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId).isPresent();
        if (isApplied) {
            throw new GeneralException(Code.CREW_ALREADY_APPLIED, "이미 신청한 크루입니다.");
        }

        boolean isAutoApproval = crew.getAutoApproval();
        if (isAutoApproval) {
            crewMemberRepository.save(
                CrewMember.builder()
                    .member(memberRepository.findById(memberId).orElseThrow(
                    () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
                ))
                    .crew(crew)
                    .role(CrewMemberRole.MEMBER)
                    .build()
            );
            message = "가입이 완료되었습니다.";
        } else {
            if (messageRequestDto == null) {
                throw new GeneralException(Code.APPLY_WITHOUT_MESSAGE, "크루에 가입하기 위해서는 참여 신청 문구가 필요합니다.");
            }

            applyMessageRepository.save(
                ApplyMessage.builder()
                    .message(messageRequestDto.getMessage())
                    .crew(crew)
                    .member(memberRepository.findById(memberId).orElseThrow(
                        () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")))
                    .build()
            );

            message = "신청이 완료되었습니다.";
        }

        return Collections.singletonMap("message", message);
    }

    public Map<String, String> uploadAndAnalyzeVoiceFile(Long reportId, MultipartFile file) {
        if (file.getSize() == 0) {
            throw new GeneralException(Code.FILE_IS_EMPTY, "음성 파일이 비어있습니다.");
        }

        Long memberId = SecurityUtil.getMemberId();

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm"));
        String filePath = String.format("reports/%s/members/%s/%s/%s.wav", String.valueOf(reportId), String.valueOf(memberId), fileName, fileName);
        String fileUrl = s3UploaderService.uploadFile(filePath, file);

        // TODO: 더미 데이터 & API URL 수정
        String fastApiUrl = "http://localhost:8000/api/voice-analysis";

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


    public Map<String, List<MemberResponseDto>> getWaitingList(Long crewId) {
        Long meberId = SecurityUtil.getMemberId();
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, meberId)
            .orElseThrow(
                () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        if (!crewMember.getRole().equals(CrewMemberRole.LEADER)) {
            throw new GeneralException(Code.CREW_MEMBER_NOT_LEADER, "크루장만 가입 승인 대기 인원을 조회할 수 있습니다.");
        }

        List<MemberResponseDto> members = applyMessageRepository.getByCrewIdAndStatus(crewId, ApplyMessageStatus.WAITING).stream()
            .map(ApplyMessage::getMember)
            .map(member -> MemberResponseDto.waitingListResponse(crewId, member, applyMessageRepository))
            .toList();

        return Collections.singletonMap("members", members);
    }

    public Map<String, String> acceptOrReject(Long crewId, Long memberId, StatusRequestDto statusRequestDto) {
        Long meberId = SecurityUtil.getMemberId();
        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, meberId)
            .orElseThrow(
                () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        if (!crewMember.getRole().equals(CrewMemberRole.LEADER)) {
            throw new GeneralException(Code.CREW_MEMBER_NOT_LEADER, "크루장만 신청을 승인할 수 있습니다.");
        }

        String status = statusRequestDto.getStatus();

        String message = "";

        ApplyMessage applyMessage = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId)
            .orElseThrow(
                () -> new GeneralException(Code.APPLY_NOT_FOUND, "존재하지 않는 신청입니다.")
            );

        if (status.equals("accept")) {
            applyMessage.acceptOrReject(status);
            crewMemberRepository.save(CrewMember.builder()
                .member(applyMessage.getMember())
                .crew(applyMessage.getCrew())
                .role(CrewMemberRole.MEMBER)
                .build()
            );

            message = "승인이 완료되었습니다.";
        } else if (status.equals("reject")) {
            applyMessage.acceptOrReject(status);
            message = "거절이 완료되었습니다.";
        }

        return Collections.singletonMap("message", message);
    }

    public Map<String, String> accuse(Long crewId, AccuseRequestDto accuseRequestDto) {
        Long memberId = SecurityUtil.getMemberId();

        crewAccuseRepository.save(CrewAccuse.builder()
            .type(CrewAccuseType.valueOf(accuseRequestDto.getType()))
            .description(accuseRequestDto.getDescription())
            .crew(crewRepository.findById(crewId).orElseThrow(
                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
            ))
            .member(memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
            ))
            .build()
        );

        return Collections.singletonMap("message", "신고가 완료되었습니다.");
    }

    public Map<String, String> withdrawal(Long crewId) {
        Long memberId = SecurityUtil.getMemberId();

        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        if (crewMember.getRole().equals(CrewMemberRole.LEADER)) {
            throw new GeneralException(Code.CREW_LEADER_CANNOT_WITHDRAW, "크루장은 크루를 탈퇴할 수 없습니다.");
        }

        crewMember.withdrawal();

        return Collections.singletonMap("message", "크루 탈퇴가 완료되었습니다.");
    }

    public Map<String, String> forceWithdrawal(Long crewId, Long crewMemberId) {
        Long memberId = SecurityUtil.getMemberId();

        boolean isLeader = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).orElseThrow(
            () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."))
            .getRole().equals(CrewMemberRole.LEADER);

        if (!isLeader) {
            throw new GeneralException(Code.CREW_MEMBER_NOT_LEADER, "크루장만 강제 탈퇴를 할 수 있습니다.");
        }

        CrewMember crewMember = crewMemberRepository.findById(crewMemberId).orElseThrow(
            () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        crewMember.withdrawal();
        
        return Collections.singletonMap("message", "강제 탈퇴가 완료되었습니다.");
    }

    public MemberProfileResponseDto getMemberProfile(String language, Long crewId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        List<MemberKeyword> memberInterest = memberKeywordRepository.findAllByMemberId(memberId);

        CrewMember crewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_NOT_JOINED, "해당 멤버는 해당 크루의 멤버가 아닙니다."));

        List<Keyword> keywords = memberInterest.stream()
                .map(MemberKeyword::getKeyword)
                .toList();

        List<Map<String, String>> interests = keywords.stream()
                .map(keyword -> {
                    Map<String, String> interest = new HashMap<>();
                    if ("ko".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getKoreanName());
                        interest.put("tag", keyword.name());
                    } else if ("en".equals(language)) {
                        interest.put("emoji", keyword.getEmoji());
                        interest.put("name", keyword.getEnglishName());
                        interest.put("tag", keyword.name());
                    }
                    return interest;
                })
                .toList();


        return MemberProfileResponseDto.getCrewMemberProfile(member, crewMember, interests);
    }

    public Map<String, String> createReport(Long reportId) {

        LocalDateTime endAt = LocalDateTime.now();

        List<VoiceFile> voiceFiles = voiceFileRepository.getAllByCrewReportId(reportId);
        List<String> fileUrls = voiceFiles.stream()
                .map(VoiceFile::getFileUrl)
                .toList();

        // TODO: 더미 데이터 & API URL 수정
        String fastApiUrl = "http://localhost:8000/api/crew-report";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(fileUrls, headers);
        String response = restTemplate.postForObject(fastApiUrl, requestEntity, String.class);

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
                        .endAt(endAt)
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
        // TODO: 로직 추가하기
        List<Map<String, Object>> keyword = new ArrayList<>();

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
            .toList();

        return CrewReportResponseDto.crewReportResponse(info, members, bubbleChart, keyword, languageRatio, feedbacks);
    }

    public Map<String, List<CrewReportResponseDto>> getAllReports(Long crewId, String sort) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );
        List<CrewReport> crewReports = crewReportRepository.findAllByCrew(crew).orElseThrow(
            () -> new GeneralException(Code.REPORT_NOT_FOUND, "크루 리포트가 존재하지 않습니다.")
        );

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

                CrewReportAnalysis crewReportAnalysis = crewReportAnalysisRepository.findByCrewReport(
                    crewReport).orElseThrow(
                    () -> new GeneralException(Code.REPORT_ANALYSIS_IS_NULL, "존재하지 않는 리포트 분석입니다.")
                );
                Map<String, String> info = getReportInfo(crewReport, crewReportAnalysis);
                return CrewReportResponseDto.crewReportListResponse(crewReport.getId(), generated,
                    members, info);
            })
            .sorted(Comparator.comparing((CrewReportResponseDto report) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(report.getInfo().get("createdAt"), formatter);
            }).reversed())
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

        Duration duration = Duration.between(crewReport.getStartedAt(), crewReportAnalysis.getEndAt());
        String durationTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        info.put("durationTime", durationTime);

        return info;
    }
}
