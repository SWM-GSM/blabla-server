package com.gsm.blabla.member.domain.nickname;

import java.security.SecureRandom;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {

    RED("빨간색", "Red"),
    ORANGE("주황색", "Orange"),
    YELLOW("노란색", "Yellow"),
    GREEN("초록색", "Green"),
    BLUE("파란색", "Blue"),
    PURPLE("보라색", "Purple"),
    PINK("분홍색", "Pink"),
    SKYBLUE("하늘색", "Skyblue"),
    LIGHTGREEN("연두색", "Lightgreen"),
    ;

    private final String koreanName;
    private final String englishName;

    public static String getRandomColor(String language) {
        List<Color> colors = List.of(Color.values());
        int randomIndex = new SecureRandom().nextInt(colors.size());

        if (language.equals("ko")) {
            return colors.get(randomIndex).koreanName;
        } else {
            return colors.get(randomIndex).englishName;
        }
    }
}
