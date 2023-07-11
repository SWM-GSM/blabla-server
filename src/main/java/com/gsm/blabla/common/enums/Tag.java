package com.gsm.blabla.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tag {
    IMPROVEMENT("\uD83D\uDCDA", "자기계발", "Improvement"),
    GAME("\uD83C\uDFAE", "게임", "Game"),
    TRAVEL("\uD83D\uDE85", "여행", "Travel"),
    FILM_MUSIC("\uD83C\uDFA5", "영화/음악", "Film·Music"),
    FRIENDSHIP("\uD83D\uDC36", "친목", "Friendship"),
    CULTURE("\uD83C\uDFA8", "문화", "Culture"),
    RELATIONSHIP("\uD83D\uDC96", "연애", "Relationship"),
    FOOD("\uD83C\uDF55", "음식", "Food"),
    SPORTS("\uD83D\uDEB4\u200D♂️", "스포츠", "Sports"),
    FINANCIAL("\uD83D\uDCB0", "재테크", "Financial")
    ;

    private final String emoji;
    private final String koreanName;
    private final String englishName;

}
