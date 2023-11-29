package com.gsm.blabla.content.api;

import com.gsm.blabla.content.dto.*;
import com.gsm.blabla.crew.dto.ScheduleRequestDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentControllerTest extends ControllerTestSupport {

    @DisplayName("컨텐츠 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getContents() throws Exception {
        // given
        ContentsResponseDto contentsResponseDto1 = createContentsResponseDto(
                1L, "인턴", "영화 인턴을 통해 비즈니스 표현을 배워봅시다.", 25.0);
        ContentsResponseDto contentsResponseDto2 = createContentsResponseDto(
                2L, "셜록", "셜록을 통해 영국 억양과 일상 표현을 배워봅시다.", 0.0);
        Map<String, List<ContentsResponseDto>> result = Map.of(
                "contents", List.of(contentsResponseDto1, contentsResponseDto2));
        given(contentService.getContents("ko")).willReturn(result);

        // when // then
        mockMvc.perform(
                        get("/api/v1/contents").header("Content-Language", "ko")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.contents").isNotEmpty())
                .andExpect(jsonPath("$.data.contents").isArray())
                .andExpect(jsonPath("$.data.contents[0].id").value(1L))
                .andExpect(jsonPath("$.data.contents[0].title").value("인턴"))
                .andExpect(jsonPath("$.data.contents[0].description").value("영화 인턴을 통해 비즈니스 표현을 배워봅시다."))
                .andExpect(jsonPath("$.data.contents[0].progress").value(25.0))
                .andExpect(jsonPath("$.data.contents[1].id").value(2L))
                .andExpect(jsonPath("$.data.contents[1].title").value("셜록"))
                .andExpect(jsonPath("$.data.contents[1].description").value("셜록을 통해 영국 억양과 일상 표현을 배워봅시다."))
                .andExpect(jsonPath("$.data.contents[1].progress").value(0.0));
    }

    @DisplayName("세부 컨텐츠 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getContentDetails() throws Exception {
        // given
        ContentDetailDto contentDetailDto1 = createcontentDetailDto(
                "시간 약속 정하기", "오스틴과 벤은 시간 약속을 정한다.", true);
        ContentDetailDto contentDetailDto2 = createcontentDetailDto(
                "인사 표현 배우기", "처음 회사에 들어간 벤은 동료들에게 인사를 한다.", false);
        ContentDetailsResponseDto contentDetailsResponseDto = ContentDetailsResponseDto.builder()
                .title("인턴")
                .description("영화 인턴을 통해 비즈니스 표현을 배워봅시다.")
                .thumbnailUrl("https://img.youtube.com/vi/0B2JUyXVp-c/hqdefault.jpg")
                .contentDetails(List.of(contentDetailDto1, contentDetailDto2))
                .build();

        given(contentService.getContentDetails(any(Long.class))).willReturn(contentDetailsResponseDto);

        // when // then
        mockMvc.perform(
                        get("/api/v1/contents/{contentId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.title").value("인턴"))
                .andExpect(jsonPath("$.data.description").value("영화 인턴을 통해 비즈니스 표현을 배워봅시다."))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("https://img.youtube.com/vi/0B2JUyXVp-c/hqdefault.jpg"))
                .andExpect(jsonPath("$.data.contentDetails").isNotEmpty())
                .andExpect(jsonPath("$.data.contentDetails").isArray())
                .andExpect(jsonPath("$.data.contentDetails[0].title").value("시간 약속 정하기"))
                .andExpect(jsonPath("$.data.contentDetails[0].description").value("오스틴과 벤은 시간 약속을 정한다."))
                .andExpect(jsonPath("$.data.contentDetails[0].isCompleted").value(true))
                .andExpect(jsonPath("$.data.contentDetails[1].title").value("인사 표현 배우기"))
                .andExpect(jsonPath("$.data.contentDetails[1].description").value("처음 회사에 들어간 벤은 동료들에게 인사를 한다."))
                .andExpect(jsonPath("$.data.contentDetails[1].isCompleted").value(false));
    }

    @DisplayName("세부 컨텐츠를 조회한다.")
    @Test
    @WithCustomMockUser
    void getContentDetail() throws Exception {
        // given
        ContentDetailResponseDto contentDetailsResponseDto = ContentDetailResponseDto.builder()
                .title("인턴")
                .description("영화 인턴을 통해 비즈니스 표현을 배워봅시다.")
                .guideSentence("저는 줄스 오스틴입니다. 저는 About the Fit의 창업자입니다.")
                .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                .contentUrl("https://youtu.be/0B2JUyXVp-c")
                .startedAtSec(42010)
                .stoppedAtSec(42020)
                .endedAtSec(42025)
                .build();
        given(contentService.getContentDetail(any(Long.class))).willReturn(contentDetailsResponseDto);

        // when // then
        mockMvc.perform(
                get("/api/v1/contents/detail/{contentDetailId}", 1L)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.title").value("인턴"))
                .andExpect(jsonPath("$.data.description").value("영화 인턴을 통해 비즈니스 표현을 배워봅시다."))
                .andExpect(jsonPath("$.data.guideSentence").value("저는 줄스 오스틴입니다. 저는 About the Fit의 창업자입니다."))
                .andExpect(jsonPath("$.data.targetSentence").value("I'm Jules Ostin. I'm the founder of About the Fit."))
                .andExpect(jsonPath("$.data.contentUrl").value("https://youtu.be/0B2JUyXVp-c"))
                .andExpect(jsonPath("$.data.startedAtSec").value(42010))
                .andExpect(jsonPath("$.data.stoppedAtSec").value(42020))
                .andExpect(jsonPath("$.data.endedAtSec").value(42025));
    }

    @DisplayName("연습실 피드백을 생성한다.")
    @Test
    @WithCustomMockUser
    void createFeedback() throws Exception {
        // given
        MemberContentDetailResponseDto memberContentDetailResponseDto = MemberContentDetailResponseDto.builder()
                        .contextRating(2)
                        .userSentence("I am Jules Ostin. I'm the ceo of About the Fit.")
                        .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                        .longFeedback("test long feedback")
                        .build();
        UserSentenceRequestDto userSentenceRequestDto = UserSentenceRequestDto.builder()
                .userSentence("I am Jules Ostin. I'm the ceo of About the Fit.")
                .build();
        given(contentService.createFeedback(any(Long.class), any(UserSentenceRequestDto.class))).willReturn(memberContentDetailResponseDto);

        // when // then
        mockMvc.perform(
                        post("/api/v1/contents/detail/{contentDetailId}/feedback", 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userSentenceRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.contextRating").value(2))
                .andExpect(jsonPath("$.data.userSentence").value("I am Jules Ostin. I'm the ceo of About the Fit."))
                .andExpect(jsonPath("$.data.targetSentence").value("I'm Jules Ostin. I'm the founder of About the Fit."))
                .andExpect(jsonPath("$.data.longFeedback").value("test long feedback"));
    }

    @DisplayName("연습실 피드백을 조회한다.")
    @Test
    @WithCustomMockUser
    void getFeedback() throws Exception {
        // given
        MemberContentDetailResponseDto memberContentDetailResponseDto = MemberContentDetailResponseDto.builder()
                .contextRating(2)
                .userSentence("I am Jules Ostin. I'm the ceo of About the Fit.")
                .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
                .longFeedback("test long feedback")
                .build();
        given(contentService.getFeedback(any(Long.class))).willReturn(memberContentDetailResponseDto);

        // when // then
        mockMvc.perform(
                        get("/api/v1/contents/detail/{contentDetailId}/feedback", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.contextRating").value(2))
                .andExpect(jsonPath("$.data.userSentence").value("I am Jules Ostin. I'm the ceo of About the Fit."))
                .andExpect(jsonPath("$.data.targetSentence").value("I'm Jules Ostin. I'm the founder of About the Fit."))
                .andExpect(jsonPath("$.data.longFeedback").value("test long feedback"));
    }

    @DisplayName("연습 기록을 저장한다.")
    @Test
    @WithCustomMockUser
    void createPracticeHistory() throws Exception {
        // given
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.wav", "audio/wav", "test1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.wav", "audio/wav", "test2".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("files", "test3.wav", "audio/wav", "test3".getBytes());

        given(contentService.savePracticeHistory(any(Long.class), ArgumentMatchers.<MultipartFile>anyList()))
                .willReturn(Collections.singletonMap("message", "연습 기록 음성 파일이 저장 완료되었습니다."));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v1/contents/detail/{contentDetailId}/practice", 1L)
                                .file(file1)
                                .file(file2)
                                .file(file3)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.message").value("연습 기록 음성 파일이 저장 완료되었습니다."));
    }

}
