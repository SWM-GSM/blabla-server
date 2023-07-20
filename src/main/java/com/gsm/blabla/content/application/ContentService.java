package com.gsm.blabla.content.application;

import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.dto.ContentResponseDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {
    private final ContentRepository contentRepository;

    public Map<String, ContentResponseDto> get(Long contentId) {
        Map<String, ContentResponseDto> result = new HashMap<>();

        Content content = contentRepository.findById(contentId).orElseThrow(
                () -> new GeneralException(Code.CONTENT_NOT_FOUND, "존재하지 않는 컨텐츠입니다.")
        );

        result.put("content", ContentResponseDto.contentResponse(content));
        return result;
    }

}
