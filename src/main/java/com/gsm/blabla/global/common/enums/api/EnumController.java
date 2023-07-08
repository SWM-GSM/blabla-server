package com.gsm.blabla.global.common.enums.api;

import com.gsm.blabla.global.common.dto.DataResponseDto;
import com.gsm.blabla.global.common.enums.Interest;
import com.gsm.blabla.global.common.enums.Level;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class EnumController {

    @GetMapping("/levels/{language}")
    public DataResponseDto<Map<String, String>> getLevels(@PathVariable String language) {
        Map<String, String> map = new HashMap<>();

        for (Level level : Level.values()) {
            if (level.getLanguage().equals(language)) {
                map.put(level.getDegree(), level.getDescription());
            }
        }

        return DataResponseDto.of(map);
    }

    @GetMapping("/interests")
    public DataResponseDto<Map<String, List<String>>> getInterests() {
        Map<String, List<String>> map = new HashMap<>();

        List<String> interests = new ArrayList<>();
        List<String> characteristics = new ArrayList<>();
        List<String> hobbies = new ArrayList<>();

        for (Interest interest : Interest.values()) {
            if (Objects.equals(interest.getCategory(), "엔터테인먼트")) {
                interests.add(interest.getName());
            } else if (Objects.equals(interest.getCategory(), "성격")) {
                characteristics.add(interest.getName());
            } else {
                hobbies.add(interest.getName());
            }
        }

        map.put("엔터테인먼트", interests);
        map.put("성격", characteristics);
        map.put("취미/관심사", hobbies);

        return DataResponseDto.of(map);

    }
}
