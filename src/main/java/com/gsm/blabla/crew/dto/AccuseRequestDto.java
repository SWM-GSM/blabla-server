package com.gsm.blabla.crew.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccuseRequestDto {
    private String type;
    private String description;
}
