package com.gsm.blabla.agora.dao;

import com.gsm.blabla.agora.domain.VoiceRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRoomRepository extends JpaRepository<VoiceRoom, Long> {

    Boolean existsByMemberId(Long memberId);
    VoiceRoom findByMemberId(Long memberId);
}
