package com.gsm.blabla.agora.application;

import static org.assertj.core.api.Assertions.*;

import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.crew.dao.CrewReportRepository;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class AgoraServiceTest extends IntegrationTestSupport {
    Member member1;
    Long crewId;

    @Autowired
    private AgoraService agoraService;

    @Autowired
    private CrewReportRepository crewReportRepository;

    @BeforeEach
    void setUp() {
        member1 = createMember("cat");
    }

    @DisplayName("[POST] 보이스룸 입장을 위한 토큰을 발급 받는다.")
    @WithCustomMockUser
    @CsvSource({"true", "false"})
    @ParameterizedTest
    void create(boolean isActivated) {
        // given
        VoiceRoomRequestDto voiceRoomRequestDto = VoiceRoomRequestDto.builder()
            .isActivated(isActivated)
            .build();

        // when
        RtcTokenDto rtcTokenDto = agoraService.create(voiceRoomRequestDto);

        // then
        assertThat(rtcTokenDto.getToken()).isNotNull();
        assertThat(rtcTokenDto.getExpiresIn()).isNotNull();
    }

    @DisplayName("[POST] 보이스룸 최초 입장 시, 크루 리포트 엔티티가 저장된다.")
    @Test
    @WithCustomMockUser
    void createFirst() {
        // given
        VoiceRoomRequestDto voiceRoomRequestDto = VoiceRoomRequestDto.builder()
            .isActivated(false)
            .build();
        Long beforeTokenCreated = crewReportRepository.count();

        // when
        RtcTokenDto rtcTokenDto = agoraService.create(voiceRoomRequestDto);
        Long afterTokenCreated = crewReportRepository.count();

        // then
        assertThat(afterTokenCreated).isEqualTo(beforeTokenCreated + 1);
    }
}
