package com.gsm.blabla.agora.dao;

import com.gsm.blabla.agora.domain.VoiceRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRoomRepository extends JpaRepository<VoiceRoom, Long> {

    Boolean existsByMemberId(Long memberId);
    List<VoiceRoom> findAllByInVoiceRoom(Boolean inVoiceRoom);
    Optional<VoiceRoom> findByMemberId(Long memberId);
}
