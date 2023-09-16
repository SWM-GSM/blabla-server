package com.gsm.blabla.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.dao.ContentDetailRepository;
import com.gsm.blabla.content.dao.MemberContentDetailRepository;
import com.gsm.blabla.content.dao.PracticeHistoryRepository;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.content.domain.PracticeHistory;
import com.gsm.blabla.content.dto.*;
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
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentDetailRepository contentDetailRepository;
    private final ContentRepository contentRepository;
    private final MemberContentDetailRepository memberContentDetailRepository;
    private final MemberRepository memberRepository;
    private final PracticeHistoryRepository practiceHistoryRepository;
    private final RestTemplate restTemplate;
    private final S3UploaderService s3UploaderService;

    @Transactional(readOnly = true)
    public Map<String, List<ContentsResponseDto>> getContents(String language) {
        List<Content> contentList = contentRepository.findAllByLanguageOrderBySequence(language);

        final Long memberId = SecurityUtil.getMemberId();

        List<ContentsResponseDto> contentCategoryResponseDtoList = new ArrayList<>();
        for (Content content : contentList) {
            List<ContentDetail> contentDetailList = contentDetailRepository.findAllByContent(content);

            List<ContentDetailDto> contentViewResponseDtoList = contentDetailList.stream()
                    .map(contentDetail -> ContentDetailDto.contentViewResponse(contentDetail, memberId, memberContentDetailRepository))
                    .toList();

            contentCategoryResponseDtoList.add(ContentsResponseDto.contentCategoryResponse(content, contentViewResponseDtoList));
        }

        return Collections.singletonMap("contents", contentCategoryResponseDtoList);
    }

    @Transactional(readOnly = true)
    public ContentDetailsResponseDto getContentDetails(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(
                () -> new GeneralException(Code.CONTENT_NOT_FOUND, "존재하지 않는 컨텐츠 카테고리입니다.")
        );

        List<ContentDetail> contentDetailList = contentDetailRepository.findAllByContentOrderBySequence(content);

        final Long memberId = SecurityUtil.getMemberId();

        List<ContentDetailDto> contentDetailDtoList = contentDetailList.stream()
                .map(contentDetail -> ContentDetailDto.contentViewResponse(contentDetail, memberId, memberContentDetailRepository))
                .toList();

        return ContentDetailsResponseDto.contentListResponse(content, contentDetailDtoList);
    }

    @Transactional(readOnly = true)
    public ContentDetailResponseDto getContentDetail(Long contentDetailId) {
        ContentDetail contentDetail = contentDetailRepository.findById(contentDetailId).orElseThrow(
                () -> new GeneralException(Code.CONTENT_DETAIL_NOT_FOUND, "존재하지 않는 세부 컨텐츠입니다.")
        );
        return ContentDetailResponseDto.contentDetailResponseDto(contentDetail);
    }

    public PracticeFeedbackResponseDto createFeedback(Long contentDetailId, UserAnswerRequestDto userAnswerRequestDto) {
        ContentDetail contentDetail = contentDetailRepository.findById(contentDetailId)
                .orElseThrow(() -> new GeneralException(Code.CONTENT_DETAIL_NOT_FOUND, "존재하지 않는 세부 컨텐츠입니다.")
        );

        String language = contentDetail.getContent().getLanguage();

        PracticeFeedbackRequestDto practiceFeedbackRequestDto = PracticeFeedbackRequestDto.builder()
                .userAnswer(userAnswerRequestDto.getUserAnswer())
                .answer(contentDetail.getTargetSentence())
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

        MemberContentDetail memberContentDetail = MemberContentDetail.builder()
                .member(member)
                .contentDetail(contentDetail)
                .userAnswer(userAnswerRequestDto.getUserAnswer())
                .longFeedback(practiceFeedbackResponseDto.getLongFeedback())
                .starScore(practiceFeedbackResponseDto.getStarScore())
                .contextScore(practiceFeedbackResponseDto.getContextScore())
                .build();

        memberContentDetailRepository.save(memberContentDetail);

        return PracticeFeedbackResponseDto.of(memberContentDetail);
    }

    public PracticeFeedbackResponseDto getFeedback(Long contentDetailId) {
        Long memberId = SecurityUtil.getMemberId();

        MemberContentDetail memberContentDetail = memberContentDetailRepository.findByContentDetailIdAndMemberId(contentDetailId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_CONTENT_NOT_FOUND, "존재하지 않는 유저 컨텐츠입니다.")
        );

        return PracticeFeedbackResponseDto.of(memberContentDetail);
    }

    public Map<String, String> savePracticeHistory(Long contentDetailId, List<MultipartFile> files) {
        if (files.size() > 3) {
            throw new GeneralException(Code.PRACTICE_HISTORY_FILE_SIZE_EXCEEDED, "연습 기록 음성 파일은 최대 3개까지 저장 가능합니다.");
        }

        Long memberId = SecurityUtil.getMemberId();
        MemberContentDetail memberContentDetail = memberContentDetailRepository.findByContentDetailIdAndMemberId(contentDetailId, memberId)
                .orElseThrow(() -> new GeneralException(Code.MEMBER_CONTENT_NOT_FOUND, "존재하지 않는 유저 컨텐츠입니다.")
        );

        IntStream.range(0, files.size()).forEach(index -> {
            MultipartFile file = files.get(index);

            String fileName = String.format("members/%s/contents/%s/%s.wav", String.valueOf(memberId), String.valueOf(contentDetailId), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm'I'")).concat(String.valueOf(index + 1)));
            String fileUrl = s3UploaderService.uploadFile(fileName, file);

            practiceHistoryRepository.save(PracticeHistory.builder()
                    .memberContentDetail(memberContentDetail)
                    .practiceUrl(fileUrl)
                    .build()
            );
        });

        return Collections.singletonMap("message", "연습 기록 음성 파일이 저장 완료되었습니다.");
    }

}
