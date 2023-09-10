package com.gsm.blabla.content.api;

import com.gsm.blabla.content.dto.ContentDetailsResponseDto;
import com.gsm.blabla.content.dto.ContentsResponseDto;
import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.content.dto.ContentDetailResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
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
        Map<String, List<ContentsResponseDto>> result = Map.of("contents", List.of(new ContentsResponseDto()));
        when(contentService.getContents("ko")).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/contents").header("Content-Language", "ko")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("세부 컨텐츠 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getContentDetails() throws Exception {
        // given
        when(contentService.getContentDetails(1L)).thenReturn(new ContentDetailsResponseDto());

        // when // then
        mockMvc.perform(
                        get("/contents/{contentId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("세부 컨텐츠를 조회한다.")
    @Test
    @WithCustomMockUser
    void getContentDetail() throws Exception {
        // given
        when(contentService.getContentDetail(1L)).thenReturn(new ContentDetailResponseDto());

        // when // then
        mockMvc.perform(
                get("/contents/detail/{contentDetailId}", 1L)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isMap());
    }

}