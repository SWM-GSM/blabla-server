package com.gsm.blabla.content.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.content.domain.MemberContentDetail;
import lombok.*;

@Getter
public class AIPracticeFeedbackResponseDto {

    private Double contextScore;
    private String longFeedback;
}
