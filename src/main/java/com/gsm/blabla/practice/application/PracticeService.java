package com.gsm.blabla.practice.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.practice.dao.ContentCategoryRepository;
import com.gsm.blabla.practice.dao.ContentRepository;
import com.gsm.blabla.practice.dao.MemberContentRepository;
import com.gsm.blabla.practice.dao.PracticeHistoryRepository;
import com.gsm.blabla.practice.domain.Content;
import com.gsm.blabla.practice.domain.ContentCategory;
import com.gsm.blabla.practice.domain.MemberContent;
import com.gsm.blabla.practice.domain.PracticeHistory;
import com.gsm.blabla.practice.dto.*;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PracticeService {
    private final ContentRepository contentRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final MemberContentRepository memberContentRepository;
    private final MemberRepository memberRepository;
    private final PracticeHistoryRepository practiceHistoryRepository;
    private final RestTemplate restTemplate;
    private final S3UploaderService s3UploaderService;

    @Transactional(readOnly = true)
    public ContentResponseDto get(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(
                () -> new GeneralException(Code.CONTENT_NOT_FOUND, "존재하지 않는 컨텐츠입니다.")
        );
        return ContentResponseDto.contentResponse(content);
    }

    @Transactional(readOnly = true)
    public Map<String, List<ContentCategoryResponseDto>> getAll(String language) {
        List<ContentCategory> contentCategories = switch (language) {
            case "ko" -> contentCategoryRepository.findAllByLanguage("ko");
            case "en" -> contentCategoryRepository.findAllByLanguage("en");
            default -> new ArrayList<>();
        };

        final Long memberId = SecurityUtil.getMemberId();

        List<ContentCategoryResponseDto> contentCategoryResponseDtoList = new ArrayList<>();
        for (ContentCategory contentCategory : contentCategories) {
            List<Content> contents = contentRepository.findAllByContentCategory(contentCategory);

            List<ContentViewResponseDto> contentViewResponseDtoList = contents.stream()
                    .map(content -> ContentViewResponseDto.contentViewResponse(contentCategory, content, memberId, memberContentRepository))
                    .toList();

            contentCategoryResponseDtoList.add(ContentCategoryResponseDto.contentCategoryResponse(contentCategory, contentViewResponseDtoList));
        }

        return Collections.singletonMap("categories", contentCategoryResponseDtoList);
    }

    @Transactional(readOnly = true)
    public ContentListResponseDto getContent(String language, Long contentCategoryId) {
        ContentCategory contentCategory = contentCategoryRepository.findById(contentCategoryId).orElseThrow(
                () -> new GeneralException(Code.CONTENT_CATEGORY_NOT_FOUND, "존재하지 않는 컨텐츠 카테고리입니다.")
        );

        List<Content> contents = contentRepository.findAllByContentCategory(contentCategory);

        final Long memberId = SecurityUtil.getMemberId();

        List<ContentViewResponseDto> contentViewResponseDtoList = contents.stream()
                .map(content -> ContentViewResponseDto.contentViewResponse(contentCategory, content, memberId, memberContentRepository))
                .toList();

        return ContentListResponseDto.contentListResponse(contentCategory, contentViewResponseDtoList);
    }

//    @Transactional(readOnly = true)
//    public ContentViewResponseDto getTodayContent(String language) {
//
//        List<Content> contents = switch (language) {
//            case "ko" -> contentRepository.findAllByLanguage("ko");
//            case "en" -> contentRepository.findAllByLanguage("en");
//            default -> new ArrayList<>();
//        };
//
//        final Long memberId = SecurityUtil.getMemberId();
//
//        Optional<Content> todayContents = contents.stream()
//                .filter(content -> memberContentRepository.findByContentIdAndMemberId(content.getId(), memberId).isEmpty())
//                .findFirst();
//
//        return todayContents.map(content -> ContentViewResponseDto.contentViewResponse(content, memberId, memberContentRepository))
//                .orElse(null);
//    }

    public PracticeFeedbackResponseDto createFeedback(Long contentId, UserAnswerRequestDto userAnswerRequestDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new GeneralException(Code.CONTENT_NOT_FOUND, "존재하지 않는 컨텐츠입니다.")
        );

        String language = content.getContentCategory().getLanguage();

        PracticeFeedbackRequestDto practiceFeedbackRequestDto = PracticeFeedbackRequestDto.builder()
                .userAnswer(userAnswerRequestDto.getUserAnswer())
                .answer(content.getTargetSentence())
                .build();

        String fastApiUrl = String.format("https://z64kktsmu3.execute-api.ap-northeast-2.amazonaws.com/dev/ai/%s/feedback", language);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PracticeFeedbackRequestDto> requestEntity = new HttpEntity<>(practiceFeedbackRequestDto, headers);
        String similarityResponse = restTemplate.postForObject(fastApiUrl, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        PracticeFeedbackResponseDto practiceFeedbackResponseDto = null;
        try {
            practiceFeedbackResponseDto = objectMapper.readValue(similarityResponse, PracticeFeedbackResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
        );

        MemberContent memberContent = MemberContent.builder()
                .member(member)
                .content(content)
                .userAnswer(userAnswerRequestDto.getUserAnswer())
                .shortFeedback(null)
                .longFeedback(practiceFeedbackResponseDto.getLongFeedback())
                .starScore(practiceFeedbackResponseDto.getStarScore())
                .contextScore(practiceFeedbackResponseDto.getContextScore())
                .build();

        memberContentRepository.save(memberContent);

        return PracticeFeedbackResponseDto.of(memberContent);
    }

    public PracticeFeedbackResponseDto getFeedback(Long contentId) {
        Long memberId = SecurityUtil.getMemberId();

        MemberContent memberContent = memberContentRepository.findByContentIdAndMemberId(contentId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_CONTENT_NOT_FOUND, "존재하지 않는 유저 컨텐츠입니다.")
        );

        return PracticeFeedbackResponseDto.of(memberContent);
    }

    public Map<String, String> savePracticeHistory(Long contentId, List<MultipartFile> files) {
        if (files.size() > 3) {
            throw new GeneralException(Code.PRACTICE_HISTORY_FILE_SIZE_EXCEEDED, "연습 기록 음성 파일은 최대 3개까지 저장 가능합니다.");
        }

        Long memberId = SecurityUtil.getMemberId();
        MemberContent memberContent = memberContentRepository.findByContentIdAndMemberId(contentId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_CONTENT_NOT_FOUND, "존재하지 않는 유저 컨텐츠입니다.")
        );

        for (MultipartFile file : files) {
            String fileName = String.format("members/%s/contents/%s/%s.wav", String.valueOf(memberId), String.valueOf(contentId), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")));
            String fileUrl = s3UploaderService.uploadFile(fileName, file);

            practiceHistoryRepository.save(PracticeHistory.builder()
                    .memberContent(memberContent)
                    .practiceUrl(fileUrl)
                    .build()
            );
        }

        return Collections.singletonMap("message", "연습 기록 음성 파일이 저장 완료되었습니다.");
    }
}
