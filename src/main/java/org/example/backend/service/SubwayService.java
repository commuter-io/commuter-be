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


import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubwayService {

    private final RestTemplate restTemplate;
    private final StationRepository stationRepository;
    private final ObjectMapper objectMapper;

    @Value("${seoul.api.key}")
    private String apiKey;
    @Value("${seoul.live.key}")
    private String liveApiKey;

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
            String url = "http://swopenAPI.seoul.go.kr/api/subway/" + liveApiKey + "/json/realtimeStationArrival/0/10/" + stationName;

            String rawResponse = restTemplate.getForObject(url, String.class);
            log.info("Raw response from Seoul API for station {}: {}", stationName, rawResponse);

            RealtimeArrival response = objectMapper.readValue(rawResponse, RealtimeArrival.class);

            // API가 에러 메시지를 반환했는지 확인 (INFO-000은 성공)
            if (response.getErrorMessage() != null && !"INFO-000".equals(response.getErrorMessage().getCode())) {
                log.warn("API returned a non-successful code for station {}: {}", stationName, response.getErrorMessage().getCode());
                // INFO-200은 "해당하는 데이터가 없습니다." 이므로 빈 리스트를 반환.
                if ("INFO-200".equals(response.getErrorMessage().getCode())) {
                    return Collections.emptyList();
                }
                // 그 외 코드는 에러로 처리
                throw new CustomException(ErrorCode.API_RESULT_ERROR, response.getErrorMessage().getMessage());
            }

            if (response.getRealtimeArrivalList() != null) {
                return response.getRealtimeArrivalList();
            }

            return Collections.emptyList();

        } catch (RestClientException e) {
            log.error("API call failed for station {}: {}", stationName, e.getMessage());
            throw new CustomException(ErrorCode.API_CALL_FAILED);
        } catch (Exception e) {
            log.error("An unexpected error occurred in getRealtimeArrivals for station {}: {}", stationName, e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "실시간 도착 정보 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getStations(String line, String name) {
        List<Station> stations;
        String formattedLine = formatLineNumber(line);

        boolean hasLine = formattedLine != null && !formattedLine.isEmpty();
        boolean hasName = name != null && !name.isEmpty();

        if (hasLine && hasName) {
            stations = stationRepository.findByLineNumberAndNameContaining(formattedLine, name);
        } else if (hasLine) {
            stations = stationRepository.findByLineNumber(formattedLine);
        } else if (hasName) {
            stations = stationRepository.findByNameContaining(name);
        } else {
            stations = stationRepository.findAll();
        }

        return stations.stream()
                .map(StationResponse::fromEntity)
                .toList();
    }

    private String formatLineNumber(String line) {
        if (line == null || line.isEmpty() || !line.contains("호선")) {
            return line;
        }
        try {
            String numberStr = line.replace("호선", "").trim();
            int number = Integer.parseInt(numberStr);
            if (number > 0 && number < 10) {
                return String.format("0%d호선", number);
            }
            return line;
        } catch (NumberFormatException e) {
            return line;
        }
    }

}
