package com.gsm.blabla.global.common.api;

import com.gsm.blabla.global.common.dto.DataResponseDto;
import com.gsm.blabla.global.common.enums.Interest;
import com.gsm.blabla.global.common.enums.Level;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 코드 관련 API")
@RestController
public class EnumController {

    @Operation(summary = "레벨 별 문구 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / eng")
    @GetMapping("common/levels/{language}")
    public DataResponseDto<Map<String, String>> getLevels(@PathVariable String language) {
        Map<String, String> map = new HashMap<>();

        for (Level level : Level.values()) {
            if (level.getLanguage().equals(language)) {
                map.put(level.getDegree(), level.getDescription());
            }
        }

        return DataResponseDto.of(map);
    }

    @Operation(summary = "키워드 조회 API")
    @Parameter(name = "language", description = "언어", example = "ko / eng")
    @GetMapping("/{language}/common/keywords")
    public DataResponseDto<Map<String, List<String>>> getInterests(@PathVariable String language) {
        Map<String, List<String>> map = new HashMap<>();

        List<String> entertainments = makeKeywordList(language, "엔터테인먼트");
        List<String> characteristics = makeKeywordList(language, "성격");
        List<String> hobbies = makeKeywordList(language, "취미/관심사");

        map.put("엔터테인먼트", entertainments);
        map.put("성격", characteristics);
        map.put("취미/관심사", hobbies);

        return DataResponseDto.of(map);

    }

    private List<String> makeKeywordList(String language, String category) {
        List<String> keywords = new ArrayList<>();

        for (Interest interest : Interest.values()) {
            if (Objects.equals(language, "ko")) {
                if (Objects.equals(interest.getCategory(), category)) {
                    keywords.add(interest.getKoreanName());
                }
            } else {
                if (Objects.equals(interest.getCategory(), category)) {
                    keywords.add(interest.getEnglishName());
                }
            }
        }

        return keywords;
    }
}
