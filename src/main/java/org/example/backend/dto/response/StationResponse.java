package org.example.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.example.backend.domain.Station;

@Getter
@Builder
@Schema(description = "지하철 역 정보 응답 DTO")
public class StationResponse {

    @Schema(description = "지하철 역 외부 코드", example = "0154")
    private String stationCode;

    @Schema(description = "지하철 역명", example = "종로5가")
    private String name;

    @Schema(description = "지하철 역명 (영문)", example = "Jongno 5(o)-ga")
    private String nameEng;

    @Schema(description = "지하철 역명 (일문)", example = "チョンノオガ")
    private String nameJpn;

    @Schema(description = "호선", example = "01호선")
    private String lineNumber;

    public static StationResponse fromEntity(Station station) {
        return StationResponse.builder()
                .stationCode(station.getStationCode())
                .name(station.getName())
                .nameEng(station.getNameEng())
                .nameJpn(station.getNameJpn())
                .lineNumber(station.getLineNumber())
                .build();
    }
}
