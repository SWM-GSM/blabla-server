package com.gsm.blabla.common.application;

import com.gsm.blabla.common.dto.CommonCodeDto;
import com.gsm.blabla.common.dto.KeywordDto;
import com.gsm.blabla.common.dto.LevelDto;
import com.gsm.blabla.common.enums.Tag;
import com.gsm.blabla.common.enums.Keyword;
import com.gsm.blabla.common.enums.Level;
import com.gsm.blabla.common.enums.PreferMember;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonService {
    public Map<String, List<LevelDto>> getLevels(String language) {
        List<LevelDto> levels = Arrays.stream(Level.values())
            .filter(level -> level.getLanguage().equals(language))
            .map(level -> LevelDto.of(level.getDegree(), level.getDescription()))
            .toList();

        return Map.of("levels", levels);
    }

    public Map<String, List<KeywordDto>> getKeywords(String language) {
        Map<String, List<KeywordDto>> result = new HashMap<>();

        result.put("keywords", Arrays.asList(
            KeywordDto.of("엔터테인먼트", createKeywordDtos(language, "엔터테인먼트")),
            KeywordDto.of("성격", createKeywordDtos(language, "성격")),
            KeywordDto.of("취미/관심사", createKeywordDtos(language, "취미/관심사"))
        ));

        return result;
    }

    public Map<String, List<CommonCodeDto>> getCrewTags(String language) {
        Map<String, List<CommonCodeDto>> result = new HashMap<>();

        List<CommonCodeDto> crewTags = new ArrayList<>();
        for (Tag tag : Tag.values()) {
            if (Objects.equals(language, "ko")) {
                crewTags.add(CommonCodeDto.of(tag.getEmoji(), tag.getKoreanName(), tag.name()));
            } else {
                crewTags.add(CommonCodeDto.of(tag.getEmoji(), tag.getEnglishName(), tag.name()));
            }
        }
        result.put("tags", crewTags);

        return result;
    }

    public Map<String, List<CommonCodeDto>> getPreferMembers(String language) {
        Map<String, List<CommonCodeDto>> result = new HashMap<>();

        List<CommonCodeDto> preferMembers = new ArrayList<>();
        for (PreferMember preferMember : PreferMember.values()) {
            if (Objects.equals(language, "ko")) {
                preferMembers.add(CommonCodeDto.of(preferMember.getEmoji(), preferMember.getKoreanName(), preferMember.name()));
            } else {
                preferMembers.add(CommonCodeDto.of(preferMember.getEmoji(), preferMember.getEnglishName(), preferMember.name()));
            }
        }
        result.put("preferMembers", preferMembers);

        return result;
    }

    private List<CommonCodeDto> createKeywordDtos(String language, String category) {
        List<CommonCodeDto> keywords = new ArrayList<>();

        for (Keyword keyword : Keyword.values()) {
            if (Objects.equals(language, "ko")) {
                if (Objects.equals(keyword.getCategory(), category)) {
                    keywords.add(
                        CommonCodeDto.of(keyword.getEmoji(), keyword.getKoreanName(), keyword.name()));
                }
            } else {
                if (Objects.equals(keyword.getCategory(), category)) {
                    keywords.add(
                        CommonCodeDto.of(keyword.getEmoji(), keyword.getEnglishName(), keyword.name()));
                }
            }
        }

        return keywords;
    }
}
