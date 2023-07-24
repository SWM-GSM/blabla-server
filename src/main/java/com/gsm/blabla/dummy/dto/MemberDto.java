package com.gsm.blabla.dummy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto {
    private Long id;
    private String profileImage;
    private String nickname;
    private Integer korLevel;
    private Integer engLevel;
    private Integer signedUpAfter;
    private String countryCode;
    private List<KeywordDto> keywords;
    private String description;
    private String application;
    private Boolean isLeader;
    private String comment;
}
