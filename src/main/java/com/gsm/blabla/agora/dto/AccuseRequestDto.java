package com.gsm.blabla.agora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccuseRequestDto {

    private String category;
    private String description;
    private Long reporteeId;
}
