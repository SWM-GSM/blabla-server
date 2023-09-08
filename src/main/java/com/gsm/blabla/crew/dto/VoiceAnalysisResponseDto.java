package com.gsm.blabla.crew.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class VoiceAnalysisResponseDto {
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration totalCallTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration englishTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration koreanTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private Duration redundancyTime;
}


class LocalTimeDeserializer extends JsonDeserializer<Duration> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Duration.between(LocalTime.MIN, LocalTime.parse(p.getText(), formatter));
    }
}
