package com.gsm.blabla.crew.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CrewReportKeywordDto {
    private String keyword;
    private Long count;
}
