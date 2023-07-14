package com.gsm.blabla.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonCodeDto {
    private final String emoji;
    private final String name;
    private final String tag;

    public static CommonCodeDto of(String emoji, String name, String tag) {
        return new CommonCodeDto(emoji, name, tag);
    }
}
