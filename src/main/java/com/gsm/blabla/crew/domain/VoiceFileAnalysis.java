package com.gsm.blabla.crew.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class VoiceFileAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voice_file_id")
    private VoiceFile voiceFile;

    private Duration totalCallTime;
    private Duration koreanTime;
    private Duration englishTime;
    private Duration redundancyTime;
    private LocalDateTime createdAt;

    @Builder
    public VoiceFileAnalysis(VoiceFile voiceFile, Duration totalCallTime, Duration koreanTime, Duration englishTime, Duration redundancyTime) {
        this.voiceFile = voiceFile;
        this.totalCallTime = totalCallTime;
        this.koreanTime = koreanTime;
        this.englishTime = englishTime;
        this.redundancyTime = redundancyTime;
        this.createdAt = LocalDateTime.now();
    }
}
