package com.gsm.blabla.crew.application;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gsm.blabla.crew.dao.*;
import com.gsm.blabla.crew.domain.*;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.crew.dto.MessageRequestDto;
import com.gsm.blabla.crew.dto.VoiceAnalysisResponseDto;
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
import com.gsm.blabla.crew.dto.AccuseRequestDto;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.crew.dto.MessageRequestDto;
import com.gsm.blabla.crew.dto.StatusRequestDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import com.gsm.blabla.member.domain.Member;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.joda.time.DateTime;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
        Optional<CrewMember> crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
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

    // TODO: n + 1 문제 최적화
    @Transactional(readOnly = true)
    public Page<CrewResponseDto> getAll(String language, Pageable pageable) {
        return crewRepository.findAll(pageable).map(crew ->
            CrewResponseDto.crewListResponse(language, crew, crewMemberRepository));
    }

    // TODO: n + 1 문제 최적화
    public List<CrewResponseDto> getMyCrews() {
        Long memberId = SecurityUtil.getMemberId();
        List<CrewMember> crewMembers = crewMemberRepository.getByMemberIdAndStatus(memberId, CrewMemberStatus.JOINED);

        return crewMembers.stream()
            .map(crewMember -> CrewResponseDto.myCrewListResponse(crewMember.getCrew(), crewMemberRepository))
            .toList();
    }

    public Map<String, String> joinCrew(Long crewId, MessageRequestDto messageRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );
        String message = "";

        boolean isJoined = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId).isPresent();
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

    public Map<String, String> createCrewReport(Long crewId, Long reportId, String users, List<MultipartFile> wavFiles) {
        if (wavFiles.stream().anyMatch(wavFile -> wavFile.getSize() == 0)) {
            throw new GeneralException(Code.FILE_IS_EMPTY, "음성 파일이 비어있습니다.");
        }

        List<String> userList = Arrays.stream(users.replaceAll("[\"\\[\\]\\s]", "").split(","))
                .toList();
        List<String> fileUrls = s3UploaderService.uploadWavFiles(crewId, reportId, userList, wavFiles, "users");
        for (String fileUrl : fileUrls) {
            System.out.println("fileUrl = " + fileUrl);
        }

        // TODO: 더미 데이터 & API URL 수정
        String fastApiUrl = "http://localhost:8000/api/voice-analysis";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(fileUrls, headers);
        String voiceAnalysisResponse = restTemplate.postForObject(fastApiUrl, requestEntity, String.class);
        if (voiceAnalysisResponse == null) {
            throw new GeneralException(Code.VOICE_ANALYSIS_IS_NULL, "음성 분석 결과가 없습니다.");
        }
        String response = voiceAnalysisResponse.replaceAll("\\\\|^\"|\"$", "");

        System.out.println("response = " + response);
        
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<VoiceAnalysisResponseDto>>() {});
        List<VoiceAnalysisResponseDto> voiceAnalysisResponseList = null;
        try {
            voiceAnalysisResponseList = reader.readValue(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (voiceAnalysisResponseList == null || voiceAnalysisResponseList.size() != fileUrls.size()) {
            throw new GeneralException(Code.VOICE_ANALYSIS_IS_NULL, "음성 분석 결과가 예상한 값과 다릅니다.");
        }

        for (VoiceAnalysisResponseDto voiceAnalysisResponseDto : voiceAnalysisResponseList) {
            System.out.println("voiceAnalysisResponseDto = " + voiceAnalysisResponseDto.getRedundancyTime());
        }

        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        crewReportRepository.save(
                CrewReport.builder()
                        .crew(crewRepository.findById(crewId).orElseThrow(
                                () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
                        ))
                        .startedAt(LocalDateTime.now())
                        .endAt(LocalDateTime.now())
                        .koreanTime(Duration.ofSeconds(100))
                        .englishTime(Duration.ofSeconds(100))
                        .cloudUrl("wewesdawd")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        CrewReport crewReport = crewReportRepository.findById(reportId).orElseThrow(
                () -> new GeneralException(Code.REPORT_NOT_FOUND, "존재하지 않는 리포트입니다.")
        );

        for (VoiceAnalysisResponseDto voiceAnalysisResponseDto : voiceAnalysisResponseList) {
            int index = voiceAnalysisResponseList.indexOf(voiceAnalysisResponseDto);
            voiceFileRepository.save(
                    VoiceFile.builder()
                            .member(member)
                            .crewReport(crewReport)
                            .fileUrl(fileUrls.get(index))
                            .totalCallTime(voiceAnalysisResponseDto.getTotalCallTime())
                            .koreanTime(voiceAnalysisResponseDto.getKoreanTime())
                            .englishTime(voiceAnalysisResponseDto.getEnglishTime())
                            .redundancyTime(voiceAnalysisResponseDto.getRedundancyTime())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
        return Collections.singletonMap("message", "음성 파일이 저장되었습니다.");
    }


    public Map<String, List<MemberResponseDto>> getWaitingList(Long crewId) {

        List<MemberResponseDto> members = applyMessageRepository.getByCrewIdAndStatus(crewId, ApplyMessageStatus.WAITING).stream()
            .map(ApplyMessage::getMember)
            .map(member -> MemberResponseDto.waitingListResponse(crewId, member, applyMessageRepository))
            .toList();

        return Collections.singletonMap("members", members);
    }

    public Map<String, String> acceptOrReject(Long crewId, Long memberId, StatusRequestDto statusRequestDto) {
        Long meberId = SecurityUtil.getMemberId();
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, meberId).orElseThrow(
            () -> new GeneralException(Code.CREW_MEMBER_NOT_FOUND, "크루에서 멤버를 찾을 수 없습니다."));

        if (!crewMember.getRole().equals(CrewMemberRole.LEADER)) {
            throw new GeneralException(Code.CREW_MEMBER_NOT_LEADER, "크루장만 신청을 승인할 수 있습니다.");
        }

        String status = statusRequestDto.getStatus();

        String message = "";

        ApplyMessage applyMessage = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId).orElseThrow(
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
}
