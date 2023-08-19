package com.gsm.blabla.dummy.dto;

import com.gsm.blabla.common.enums.Keyword;
import java.util.List;
import lombok.Getter;

@Getter
public class ProfileDto {
    private String nickname;
    private String profileImage;
    private Integer korLevel;
    private Integer engLevel;
    private String countryCode;
    private List<Keyword> keywords;
    private String description;
}
