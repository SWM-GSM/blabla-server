package com.gsm.blabla.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Interest {
    MOVIE("엔터테인먼트", "\uD83C\uDFAC 영화 감상"),
    NETFLIX("엔터테인먼트", "\uD83C\uDF7F 넷플릭스"),
    KPOP("엔터테인먼트", "\uD83C\uDFA4 K-POP"),
    ANIMATION("엔터테인먼트", "\uD83D\uDC7B 애니메이션"),
    INSTAGRAM("엔터테인먼트","✨ 인스타그램"),
    GAME("엔터테인먼트", "\uD83C\uDFAE 게임"),
    YOUTUBE("엔터테인먼트", "\uD83D\uDCFD️ 유튜브"),
    DRAMA("엔터테인먼트", "\uD83E\uDD20 드라마"),
    WEBTOON("엔터테인먼트", "\uD83C\uDF10 웹툰"),
    SINGING("엔터테인먼트", "\uD83C\uDF99️ 노래 부르기"),
    PLAYING_INSTRUMENT("엔터테인먼트", "\uD83E\uDD41 악기 연주"),
    MUKBANG("엔터테인먼트", "\uD83C\uDF7D️ 먹방"),
    WATCHING_PERFORMANCE("엔터테인먼트", "\uD83C\uDFBA 공연 관람"),

    INTROVERT("성격", "\uD83D\uDE0C 내향적인"),
    OPTIMISTIC("성격", "\uD83D\uDE0A 낙천적인"),
    EMOTIONAL("성격", "\uD83D\uDE07 감성적인"),
    EXTROVERTED("성격", "\uD83D\uDE06 외향적인"),
    HUMOROUS("성격", "\uD83D\uDE1D 유머러스"),

    TRAVEL("취미/관심사", "✈️ 여행"),
    LANGUAGE_EXCHANGE("취미/관심사", "\uD83D\uDCAC 언어교환"),
    RELATIONSHIP("취미/관심사", "\uD83D\uDC81\u200D 대인관계"),
    LANGUAGE("취미/관심사", "\uD83D\uDD8B️ 어학"),
    READING("취미/관심사", "\uD83D\uDCDA 독서"),
    DRIVE("취미/관심사", "\uD83D\uDE97 드라이브"),
    SHOPPING("취미/관심사", "\uD83D\uDECD️ 쇼핑"),
    COUNSELING("취미/관심사", "\uD83D\uDCEE 고민상담"),
    COOKING("취미/관심사", "\u200D\uD83C\uDF73 요리"),
    BAKING("취미/관심사", "\uD83E\uDD50 베이킹"),
    DIY("취미/관심사", "\uD83E\uDDF6 DIY"),
    SPORTS("취미/관심사", "⛳ 스포츠"),
    CAREER("취미/관심사", "\uD83D\uDCBC 커리어"),
    FINANCE("취미/관심사", "\uD83D\uDCB0 재테크")
    ;

    private final String category;
    private final String name;

}
