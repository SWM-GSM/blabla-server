package com.gsm.blabla.common.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeywordDto {
    private final String category;
    private final List<CommonCodeDto> keyword;

    public static KeywordDto of(String category, List<CommonCodeDto> keyword) {
        return new KeywordDto(category, keyword);
    }
}
