package com.gsm.blabla.agora.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccuseCategory {
    ABUSE,
    PORN,
    CONFLICT,
    SPAM,
    ETC;
}
