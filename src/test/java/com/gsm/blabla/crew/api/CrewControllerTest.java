package com.gsm.blabla.crew.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.common.enums.PreferMember;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.crew.domain.MeetingCycle;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.member.dto.MemberRequestDto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CrewControllerTest extends ControllerTestSupport {

    MemberRequestDto memberRequestDto;

    @BeforeEach
    void setUp() {
        memberRequestDto = MemberRequestDto.builder()
            .socialLoginType("TEST")
            .nickname("테스트")
            .profileImage("cat")
            .birthDate("2001-01-01")
            .gender("male")
            .countryCode("KR")
            .korLevel(5)
            .engLevel(5)
            .pushNotification(false)
            .build();
    }

    @DisplayName("[POST] 크루를 생성한다.")
    @Test
    @WithCustomMockUser
    void create() throws Exception {
        // given
        CrewRequestDto crewRequestDto = CrewRequestDto.builder()
            .coverImage("test")
            .name("테스트")
            .description("테스트 크루입니다.")
            .meetingCycle(MeetingCycle.EVERYDAY)
            .tags(List.of(Tag.CULTURE, Tag.FILM_MUSIC))
            .maxNum(8)
            .korLevel(1)
            .engLevel(1)
            .preferMember(PreferMember.SAME_HOBBY)
            .detail("테스트 크루입니다.")
            .autoApproval(true)
            .build();

        given(crewService.create(any(CrewRequestDto.class)))
            .willReturn(Collections.singletonMap("crewId", 1L));

        // when // then
        mockMvc.perform(
            post("/crews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crewRequestDto))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.crewId").exists())
            ;
    }
}
