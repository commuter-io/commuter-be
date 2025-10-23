package org.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RealtimeArrival {

    private ErrorMessage errorMessage;
    private List<Arrival> realtimeArrivalList;

    @Getter
    @NoArgsConstructor
    public static class ErrorMessage {
        private int status;
        private String code;
        private String message;
        private String link;
        private String developerMessage;
        private int total;
    }

    @Getter
    @NoArgsConstructor
    public static class Arrival {
        private String subwayId; // 지하철호선ID
        private String updnLine; // 상하행선구분
        private String trainLineNm; // 도착지방면
        private String statnNm; // 지하철역명
        private String btrainSttus; // 열차종류(급행,ITX)
        private String barvlDt; // 열차도착예정시간 (초)
        private String arvlMsg2; // 첫번째도착메세지 (도착, 출발, 진입 등)
        private String arvlMsg3; // 두번째도착메세지 (종합운동장 도착, 12분 후 (광운대) 등)
        private String arvlCd; // 도착코드 (0:진입, 1:도착, 2:출발, 3:전역출발, 4:전역진입, 5:전역도착, 99:운행중)
    }
}
