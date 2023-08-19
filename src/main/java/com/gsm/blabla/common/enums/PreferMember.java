package com.gsm.blabla.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferMember {
    PASSIONATE("\uD83E\uDD29", "열정적인 크루원", "Who I Passionate"),
    MAKE_FRIEND("\uD83E\uDD73", "친구를 사귀고 싶은 크루원", "Who Wants Toc Make a Friend"),
    SAME_HOBBY("\uD83D\uDE0D", "같은 취미에 대해 이야기하고 싶은 크루원", "Who Wants To Talk About The Same Hobby"),
    IMPROVE_LANGUAGE_SKILL("\uD83D\uDE0E", "한국어·영어 실력 향상을 목표로 하는 크루원", "Who Aims To Improve Their Korean/English Skills"),
    IMPROVE_CONVERSATION_SKILL("\uD83E\uDD14", "실용적 회화 능력 향상에 초점을 둔 크루원", "Who Focuses On Improving Practical Conversation Skills")
    ;

    private final String emoji;
    private final String koreanName;
    private final String englishName;
}