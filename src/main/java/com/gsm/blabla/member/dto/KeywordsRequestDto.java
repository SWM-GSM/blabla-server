package com.gsm.blabla.member.dto;

import com.gsm.blabla.common.enums.Keyword;
import lombok.Getter;

import java.util.List;

@Getter
public class KeywordsRequestDto {
    private List<Keyword> keywords;
}
