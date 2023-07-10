package com.gsm.blabla.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    ONE_KOR("Lv. 1", "ko","단어를 몇 개 알고 있습니다"),
    TWO_KOR("Lv. 2","ko","몇 가지 기본 문장을 만들 수 있습니다"),
    THREE_KOR("Lv. 3","ko","기본적인 대화를 할 수 있습니다"),
    FOUR_KOR("Lv. 4","ko","어느 정도 대화를 할 수 있습니다"),
    FIVE_KOR("Lv. 5","ko","다양한 주제에 대해 편하게 말할 수 있습니다"),

    ONE_ENG("Lv. 1","en","I know only a few words"),
    TWO_ENG("Lv. 2","en","I know a few phrases and can make some sentences"),
    THREE_ENG("Lv. 3","en","I can hold basic conversations"),
    FOUR_ENG("Lv. 4","en","I can speak comfortably about diverse topics"),
    FIVE_ENG("Lv. 5","en","I can speak about almost everything I want to");

    private final String degree;
    private final String language;
    private final String description;
}
