package com.gsm.blabla.crew.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingCycle {
    AUTONOMY("자율"),
    ONCE_A_MONTH("월 1회"),
    TWICE_A_MONTH("월 2회"),
    ONCE_A_WEEK("주 1회"),
    TWO_TO_THREE_TIMES_A_WEEK("주 2~3회"),
    FOUR_TO_FIVE_TIMES_A_WEEK("주 4~5회"),
    EVERYDAY("매일")
    ;

    private final String name;
}
