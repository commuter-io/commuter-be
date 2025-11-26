package org.example.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncidentType {
    DELAY("지연"),
    ACCIDENT("사고/고장"),
    PROTEST("시위"),
    SCHEDULE_CHANGE("운행/시간표 변경"),
    NONSTOP("무정차"),
    ETC("기타");

    private final String description;

    public static IncidentType fromString(String text) {
        if (text == null) return ETC;
        if (text.contains("지연")) return DELAY;
        if (text.contains("고장") || text.contains("사고")) return ACCIDENT;
        if (text.contains("시위")) return PROTEST;
        if (text.contains("운행") || text.contains("시간표") || text.contains("변경")) return SCHEDULE_CHANGE;
        if (text.contains("무정차")) return NONSTOP;
        return ETC;
    }
}
