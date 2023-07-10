package com.gsm.blabla.common.application;

import com.gsm.blabla.common.dto.KeywordDto;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.common.enums.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonService {
    public Map<String, String> getLevels(String language) {
        Map<String, String> result = new HashMap<>();

        for (Level level : Level.values()) {
            if (level.getLanguage().equals(language)) {
                result.put(level.getDegree(), level.getDescription());
            }
        }

        return result;
    }

    public Map<String, List<KeywordDto>> getKeywords(String language) {
        Map<String, List<KeywordDto>> result = new HashMap<>();

        List<KeywordDto> entertainments = createKeywordDtos(language, "엔터테인먼트");
        List<KeywordDto> characteristics = createKeywordDtos(language, "성격");
        List<KeywordDto> hobbies = createKeywordDtos(language, "취미/관심사");

        result.put("엔터테인먼트", entertainments);
        result.put("성격", characteristics);
        result.put("취미/관심사", hobbies);

        return result;
    }
    }

    private List<KeywordDto> createKeywordDtos(String language, String category) {
        List<KeywordDto> keywords = new ArrayList<>();

        for (Keyword keyword : Keyword.values()) {
            if (Objects.equals(language, "ko")) {
                if (Objects.equals(keyword.getCategory(), category)) {
                    keywords.add(KeywordDto.of(keyword.getEmoji(), keyword.getKoreanName(), keyword.name()));
                }
            } else {
                if (Objects.equals(keyword.getCategory(), category)) {
                    keywords.add(KeywordDto.of(keyword.getEmoji(), keyword.getEnglishName(), keyword.name()));
                }
            }
        }

        return keywords;
    }
}
