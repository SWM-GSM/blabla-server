package com.gsm.blabla.agora.application;

import static org.assertj.core.api.Assertions.*;

import com.gsm.blabla.agora.dto.RtcTokenDto;
import com.gsm.blabla.agora.dto.VoiceRoomRequestDto;
import com.gsm.blabla.global.IntegrationTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import com.gsm.blabla.member.domain.Member;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgoraServiceTest extends IntegrationTestSupport {
    Member member1;
    Member member2;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        member1 = createMember("테스트1", "cat");
        member2 = createMember("테스트2", "dog");
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
    void crewReportCreatedAtFirst() {
        // given
        VoiceRoomRequestDto voiceRoomRequestDto = VoiceRoomRequestDto.builder()
            .isActivated(false)
            .build();
        long beforeTokenCreated = crewReportRepository.count();

        // when
        agoraService.create(voiceRoomRequestDto);
        long afterTokenCreated = crewReportRepository.count();

        // then
        assertThat(afterTokenCreated).isEqualTo(beforeTokenCreated + 1);
    }

    @DisplayName("[POST] 보이스룸 최초 입장 시, 보이스룸 엔티티가 저장된다.")
    @Test
    @WithCustomMockUser
    void voiceRoomcreatedAtFirst() {
        // given
        VoiceRoomRequestDto voiceRoomRequestDto = VoiceRoomRequestDto.builder()
            .isActivated(false)
            .build();
        long beforeTokenCreated = voiceRoomRepository.count();

        // when
        agoraService.create(voiceRoomRequestDto);
        long afterTokenCreated = voiceRoomRepository.count();

        // then
        assertThat(afterTokenCreated).isEqualTo(beforeTokenCreated + 1);
    }

    @DisplayName("[GET] 보이스룸에 접속한 유저 목록을 조회한다.")
    @Test
    @WithCustomMockUser
    void getMembers() {
        // given
        startVoiceRoom(now, member1);
        joinVoiceRoom(member2);

        // when
        List<MemberResponseDto> response = agoraService.getMembers().get("members");

        // then
        assertThat(response).hasSize(2);
        assertThat(response)
            .extracting("id", "nickname", "profileImage")
            .containsExactlyInAnyOrder(
                tuple(member1.getId(), member1.getNickname(), member1.getProfileImage()),
                tuple(member2.getId(), member2.getNickname(), member2.getProfileImage())
            );
    }
}
