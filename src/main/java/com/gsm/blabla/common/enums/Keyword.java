package com.gsm.blabla.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Keyword {
    FILM("엔터테인먼트", "\uD83C\uDFAC", "영화 감상", "Film"),
    NETFLIX("엔터테인먼트", "\uD83C\uDF7F", "넷플릭스", "Netflix"),
    KPOP("엔터테인먼트", "\uD83C\uDFA4", "K-Pop", "K-Pop"),
    ANIMATION("엔터테인먼트", "\uD83D\uDC7B", "애니메이션", "Animation"),
    INSTAGRAM("엔터테인먼트", "✨", "인스타그램", "Instagram"),
    GAME("엔터테인먼트", "\uD83C\uDFAE", "게임", "Game"),
    YOUTUBE("엔터테인먼트", "\uD83D\uDCFD", "️유튜브", "Youtube"),
    DRAMA("엔터테인먼트", "\uD83E\uDD20", "드라마", "Drama"),
    WEBTOON("엔터테인먼트", "\uD83C\uDF10", "웹툰", "Webtoon"),
    SING("엔터테인먼트", "\uD83C\uDF99️", "노래 부르기", "Sing"),
    INSTRUMENTS("엔터테인먼트", "\uD83E\uDD41", "악기 연주", "Instruments"),
    MUKBANG("엔터테인먼트", "\uD83C\uDF7D️", "먹방", "Mukbang"),
    CONCERT("엔터테인먼트", "\uD83C\uDFBA", "공연 관람", "Concert"),

    INTROVERTED("성격", "\uD83D\uDE0C", "내향적인", "Introverted"),
    OPTIMISTIC("성격", "\uD83D\uDE0A", "낙천적인", "Optimistic"),
    EMOTIONAL("성격", "\uD83D\uDE07", "감성적인", "Emotional"),
    OUTGOING("성격", "\uD83D\uDE06", "외향적인", "Outgoing"),
    HUMOROUS("성격", "\uD83D\uDE1D", "유머러스", "Humorous"),

    TRAVEL("취미/관심사", "✈️", "여행", "Travel"),
    LANGUAGE_EXCHANGE("취미/관심사", "\uD83D\uDCAC", "언어교환", "Language Exchange"),
    PERSONAL_RELATIONS("취미/관심사", "\uD83D\uDC81\u200D", "대인관계", "Personal Relations"),
    LANGUAGE("취미/관심사", "\uD83D\uDD8B", " 어학", "Language"),
    BOOK("취미/관심사", "\uD83D\uDCDA", "독서", "Book"),
    DRIVE("취미/관심사", "\uD83D\uDE97", "드라이브", "Drive"),
    SHOPPING("취미/관심사", "\uD83D\uDECD", " 쇼핑", "Shopping"),
    COUNSELING("취미/관심사", "\uD83D\uDCEE", "고민상담", "Counseling"),
    COOK("취미/관심사", "\u200D\uD83C\uDF73", "요리", "Cook"),
    BAKING("취미/관심사", "\uD83E\uDD50", "베이킹", "Baking"),
    DIY("취미/관심사", "\uD83E\uDDF6", "DIY", "DIY"),
    SPORTS("취미/관심사", "⛳", "스포츠", "Sports"),
    CAREER("취미/관심사", "\uD83D\uDCBC", "커리어", "Career"),
    FINANCIAL("취미/관심사", "\uD83D\uDCB0", "재테크", "Financial"),
    ;

    private final String category;
    private final String emoji;
    private final String koreanName;
    private final String englishName;
}
