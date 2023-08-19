package com.gsm.blabla.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LevelDto {
    private final int degree;
    private final String description;

    public static LevelDto of(int degree, String description) {
        return new LevelDto(degree, description);
    }
}
