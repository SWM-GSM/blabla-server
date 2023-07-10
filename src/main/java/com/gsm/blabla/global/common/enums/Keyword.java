package com.gsm.blabla.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Interest {
    FILM("엔터테인먼트", "\uD83C\uDFAC 영화 감상", "\uD83C\uDFAC Film"),
    NETFLIX("엔터테인먼트", "\uD83C\uDF7F 넷플릭스", "\uD83C\uDF7F Netflix"),
    KPOP("엔터테인먼트", "\uD83C\uDFA4 K-Pop", "\uD83C\uDFA4 K-Pop"),
    ANIMATION("엔터테인먼트", "\uD83D\uDC7B 애니메이션", "\uD83D\uDC7B Animation"),
    INSTAGRAM("엔터테인먼트","✨ 인스타그램", "✨ Instagram"),
    GAME("엔터테인먼트", "\uD83C\uDFAE 게임", "\uD83C\uDFAE Game"),
    YOUTUBE("엔터테인먼트", "\uD83D\uDCFD️ 유튜브", "\uD83D\uDCFD️ Youtube"),
    DRAMA("엔터테인먼트", "\uD83E\uDD20 드라마", "\uD83E\uDD20 Drama"),
    WEBTOON("엔터테인먼트", "\uD83C\uDF10 웹툰", "\uD83C\uDF10 Webtoon"),
    SING("엔터테인먼트", "\uD83C\uDF99️ 노래 부르기", "\uD83C\uDF99️ Sing"),
    INSTRUMENTS("엔터테인먼트", "\uD83E\uDD41 악기 연주", "\uD83E\uDD41 Instruments"),
    MUKBANG("엔터테인먼트", "\uD83C\uDF7D️ 먹방", "\uD83C\uDF7D️ Mukbang"),
    CONCERT("엔터테인먼트", "\uD83C\uDFBA 공연 관람", "\uD83C\uDFBA Concert"),

    INTROVERTED("성격", "\uD83D\uDE0C 내향적인", "\uD83D\uDE0C Introverted"),
    OPTIMISTIC("성격", "\uD83D\uDE0A 낙천적인", "\uD83D\uDE0A Optimistic"),
    EMOTIONAL("성격", "\uD83D\uDE07 감성적인", "\uD83D\uDE07 Emotional"),
    OUTGOING("성격", "\uD83D\uDE06 외향적인", "\uD83D\uDE06 Outgoing"),
    HUMOROUS("성격", "\uD83D\uDE1D 유머러스", "\uD83D\uDE1D Humorous"),

    TRAVEL("취미/관심사", "✈️ 여행", "✈️ Travel"),
    LANGUAGE_EXCHANGE("취미/관심사", "\uD83D\uDCAC 언어교환", "\uD83D\uDCAC Language Exchange"),
    PERSONAL_RELATIONS("취미/관심사", "\uD83D\uDC81\u200D 대인관계", "\uD83D\uDC81\u200D Personal Relations"),
    LANGUAGE("취미/관심사", "\uD83D\uDD8B️ 어학", "\uD83D\uDD8B️ Language"),
    BOOK("취미/관심사", "\uD83D\uDCDA 독서", "\uD83D\uDCDA Book"),
    DRIVE("취미/관심사", "\uD83D\uDE97 드라이브", "\uD83D\uDE97 Drive"),
    SHOPPING("취미/관심사", "\uD83D\uDECD️ 쇼핑", "\uD83D\uDECD️ Shopping"),
    COUNSELING("취미/관심사", "\uD83D\uDCEE 고민상담", "\uD83D\uDCEE Counseling"),
    COOK("취미/관심사", "\u200D\uD83C\uDF73 요리", "\u200D\uD83C\uDF73 Cook"),
    BAKING("취미/관심사", "\uD83E\uDD50 베이킹", "\uD83E\uDD50 Baking"),
    DIY("취미/관심사", "\uD83E\uDDF6 DIY", "\uD83E\uDDF6 DIY"),
    SPORTS("취미/관심사", "⛳ 스포츠", "⛳ Sports"),
    CAREER("취미/관심사", "\uD83D\uDCBC 커리어", "\uD83D\uDCBC Career"),
    FINANCIAL("취미/관심사", "\uD83D\uDCB0 재테크", "\uD83D\uDCB0 Financial"),
    ;

    private final String category;
    private final String koreanName;
    private final String englishName;

}
