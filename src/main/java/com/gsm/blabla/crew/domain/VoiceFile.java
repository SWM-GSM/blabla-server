package com.gsm.blabla.crew.domain;

import com.gsm.blabla.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_report_id")
    private CrewReport crewReport;

    private String fileUrl;
    private String feedback;
    private String targetToken;

    @Builder
    public VoiceFile(Member member, CrewReport crewReport, String fileUrl, String targetToken) {
        this.member = member;
        this.crewReport = crewReport;
        this.fileUrl = fileUrl;
        this.targetToken = targetToken;
    }

    public void createFeedback(String feedback) {
        this.feedback = feedback;
    }

}
