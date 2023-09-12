package com.gsm.blabla.member.domain.nickname;

import java.security.SecureRandom;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Animal {

    BEAR("곰", "Bear"),
    CHICK("병아리", "Chick"),
    COW("소", "Cow"),
    DOG("강아지", "Dog"),
    DINO("공룡", "Dino"),
    DRAGON("용", "Dragon"),
    FOX("여우", "Fox"),
    HAMSTER("햄스터", "Hamster"),
    KOALA("코알라", "Koala"),
    LION("사자", "Lion"),
    MONKEY("원숭이", "Monkey"),
    MOUSE("쥐", "Mouse"),
    PANDA("판다", "Panda"),
    PENGUIN("펭귄", "Penguin"),
    PIG("돼지", "Pig"),
    PIGEON("비둘기", "Pigeon"),
    RACOON("너구리", "Racoon"),
    RABBIT("토끼", "Rabbit"),
    TIGER("호랑이", "Tiger"),
    WHITEBEAR("백곰", "Whitebear"),
    WOLF("늑대", "Wolf")
    ;

    private final String koreanName;
    private final String englishName;

    public static Animal getRandomAnimal() {
        List<Animal> animals = List.of(Animal.values());
        int randomIndex = new SecureRandom().nextInt(animals.size());

        return animals.get(randomIndex);
    }
}
