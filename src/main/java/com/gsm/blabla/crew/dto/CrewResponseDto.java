package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewResponseDto {
    private Long id;
    private String name;
    private String description;
    private String meetingCycle;
    private int maxNum;
    private int currentNum;
    private int korLevel;
    private String korLevelText;
    private int engLevel;
    private String engLevelText;
    private String preferMember;
    private String detail;
    private boolean autoApproval;
    private String coverUrl;
    private LocalDateTime createdAt;
    private List<MemberResponseDto> members;
    private List<String> tags;

    public static CrewResponseDto crewResponse(String language, Crew crew,
        CrewMemberRepository crewMemberRepository) {
        return CrewResponseDto.builder()
            .name(crew.getName())
            .description(crew.getDescription())
            .meetingCycle(crew.getMeetingCycle().getName())
            .maxNum(crew.getMaxNum())
            .currentNum(crewMemberRepository.countCrewMembersByCrewIdAndStatus(crew.getId(),
                CrewMemberStatus.JOINED))
            .korLevel(crew.getKorLevel())
            .korLevelText(getLevelDescription(language, crew.getKorLevel()))
            .engLevel(crew.getEngLevel())
            .engLevelText(getLevelDescription(language, crew.getEngLevel()))
            .preferMember(Objects.equals(language, "ko") ? crew.getPreferMember().getKoreanName()
                : crew.getPreferMember().getEnglishName())
            .detail(crew.getDetail())
            .autoApproval(crew.isAutoApproval())
            .coverUrl(crew.getCoverUrl())
            .members(crew.getCrewMembers().stream()
                    .map(crewMember -> MemberResponseDto.crewProfileResponse(crew.getId(), crewMember.getMember(),
                crewMemberRepository))
                .toList()
            )
            .tags(crew.getCrewTags().stream()
                .map(crewTag -> language.equals("ko") ? crewTag.getTag().getKoreanName() : crewTag.getTag().getEnglishName()
                ).toList()
            )
            .build();
    }

    public static CrewResponseDto crewListResponse(String language, Crew crew,
        CrewMemberRepository crewMemberRepository) {
        return CrewResponseDto.builder()
            .id(crew.getId())
            .name(crew.getName())
            .korLevel(crew.getKorLevel())
            .engLevel(crew.getEngLevel())
            .maxNum(crew.getMaxNum())
            .currentNum(crewMemberRepository.countCrewMembersByCrewIdAndStatus(crew.getId(),
                CrewMemberStatus.JOINED))
            .createdAt(crew.getCreatedAt())
            .coverUrl(crew.getCoverUrl())
            .tags(crew.getCrewTags().stream()
                .map(crewTag -> language.equals("ko") ? crewTag.getTag().getKoreanName() : crewTag.getTag().getEnglishName()
                ).toList()
            )
            .build();

    }
    public static String getLevelDescription(String language, int degree) {
        for (Level level : Level.values()) {
            if (level.getLanguage().equals(language) && level.getDegree() == degree) {
                return level.getDescription();
            }
        }
        return null;
    }
}
