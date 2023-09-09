package com.gsm.blabla.member.domain.nickname;

import java.security.SecureRandom;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Adjective {

    HAPPY("행복한", "Happy"),
    PRETTY("예쁜", "Pretty"),
    HANDSOME("잘생긴", "Handsome"),
    CUTE("귀여운", "Cute"),
    LOVELY("사랑스러운", "Lovely"),
    PURE("순수한", "Pure"),
    HOPEFUL("희망찬", "Hopeful"),
    EXCITING("신나는", "Exciting"),
    STRONG("힘쎈", "Strong"),
    WISE("슬기로운", "Wise"),
    SPARKLING("반짝이는", "Sparkling"),
    BOLD("대담한", "Bold"),
    PASSIONATE("열정적인", "Passionate"),
    MYSTERIOUS("신비로운", "Mysterious"),
    BRIGTH("밝은", "Bright"),
    PEACEFUL("평화로운", "Peaceful"),
    FRESH("신선한", "Fresh"),
    ORDINARY("평범한", "Ordinary"),
    NERDY("똑똑한", "Nerdy"),
    SIMPLE("단순한", "Simple");

    private final String koreanName;
    private final String englishName;

    public static String getRandomAdjective(String language) {
        List<Adjective> adjectives = List.of(Adjective.values());
        int randomIndex = new SecureRandom().nextInt(adjectives.size());

        if (language.equals("ko")) {
            return adjectives.get(randomIndex).koreanName;
        } else {
            return adjectives.get(randomIndex).englishName;
        }
    }
}
