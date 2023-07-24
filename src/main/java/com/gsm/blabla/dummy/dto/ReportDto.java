package com.gsm.blabla.dummy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsm.blabla.dummy.api.DummyController;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportDto {
    private Long id;
    private Info info;
    private List<MemberDto> members;
    private String bubbleChart;
    private List<Keyword> keyword;
    private LanguageRatio languageRatio;
    private List<MemberDto> feedbacks;

    @Getter
    @Builder
    public static class Info {
        private String createdAt;
        private String durationTime;
    }

    @Getter
    @Builder
    public static class Keyword {
        private String name;
        private int count;
    }

    @Getter
    @Builder
    public static class LanguageRatio {
        private double korean;
        private double english;
    }
}
