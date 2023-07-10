package com.gsm.blabla.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CrewTagDto {
    private final String emoji;
    private final String name;
    private final String tag;

    public static CrewTagDto of(String emoji, String name, String tag) {
        return new CrewTagDto(emoji, name, tag);
    }
}
