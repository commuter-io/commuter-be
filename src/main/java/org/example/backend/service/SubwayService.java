package org.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.Station;
import org.example.backend.domain.StationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.backend.domain.Station;
import org.example.backend.dto.response.RealtimeArrival;
import org.example.backend.dto.response.StationResponse;
import org.example.backend.exception.CustomException;
import org.example.backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class SubwayService {

    private final RestTemplate restTemplate;
    private final StationRepository stationRepository;

    @Value("${seoul.api.key}")
    private String apiKey;

    private static final String SEOUL_API_BASE_URL = "http://openapi.seoul.go.kr:8088/";

    @Transactional
    public void updateStationDatabase() {
        String url = SEOUL_API_BASE_URL + apiKey + "/json/SearchSTNBySubwayLineInfo/1/1000/";

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            // API 결과 코드 확인
            JsonNode resultCodeNode = response.path("SearchSTNBySubwayLineInfo").path("RESULT").path("CODE");
            if (!"INFO-000".equals(resultCodeNode.asText())) {
                String errorMessage = response.path("SearchSTNBySubwayLineInfo").path("RESULT").path("MESSAGE").asText();
                throw new CustomException(ErrorCode.API_RESULT_ERROR, errorMessage);
            }

            JsonNode stationList = response.path("SearchSTNBySubwayLineInfo").path("row");

            if (stationList.isArray()) {
                for (JsonNode stationNode : stationList) {
                    String stationCode = stationNode.path("STATION_CD").asText();
                    stationRepository.findByStationCode(stationCode).orElseGet(() -> {
                        Station station = Station.builder()
                                .name(stationNode.path("STATION_NM").asText())
                                .lineNumber(stationNode.path("LINE_NUM").asText())
                                .stationCode(stationCode)
                                .nameEng(stationNode.path("STATION_NM_ENG").asText())
                                .nameJpn(stationNode.path("STATION_NM_JPN").asText())
                                .build();
                        return stationRepository.save(station);
                    });
                }
            }
        } catch (RestClientException e) {
            log.error("API call failed: {}", e.getMessage());
            throw new CustomException(ErrorCode.API_CALL_FAILED);
        } catch (Exception e) {
            log.error("An unexpected error occurred in updateStationDatabase: {}", e.getMessage());
            throw new CustomException(ErrorCode.API_CALL_FAILED, "데이터베이스 업데이트 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
    public void scheduleStationUpdates() {
        updateStationDatabase();
    }

    public List<RealtimeArrival.Arrival> getRealtimeArrivals(String stationName) {
        try {
            String encodedStationName = java.net.URLEncoder.encode(stationName, java.nio.charset.StandardCharsets.UTF_8);
            String url = "http://swopenAPI.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/10/" + encodedStationName;

            RealtimeArrival response = restTemplate.getForObject(url, RealtimeArrival.class);

            if (response != null && response.getRealtimeArrivalList() != null) {
                return response.getRealtimeArrivalList();
            }
        } catch (Exception e) {
            // TODO: 예외 처리 로직 추가 (e.g., 로깅)
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getStations(String line, String name) {
        List<Station> stations;

        if (line != null && !line.isEmpty()) {
            stations = stationRepository.findByLineNumber(line);
        } else if (name != null && !name.isEmpty()) {
            stations = stationRepository.findByNameContaining(name);
        } else {
            stations = stationRepository.findAll();
        }

        return stations.stream()
                .map(StationResponse::fromEntity)
                .toList();
    }

}
