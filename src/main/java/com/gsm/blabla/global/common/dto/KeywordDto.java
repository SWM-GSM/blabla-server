package com.gsm.blabla.global.common.dto;

import com.gsm.blabla.global.common.enums.Keyword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeywordDto {
    private final String emoji;
    private final String keyword;
    private final String tag;

    public static KeywordDto of(String emoji, String keyword, String tag) {
        return new KeywordDto(emoji, keyword, tag);
    }
}
