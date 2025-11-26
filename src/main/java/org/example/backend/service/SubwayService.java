package org.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.*;
import org.example.backend.dto.response.RealtimeArrival;
import org.example.backend.dto.response.StationResponse;
import org.example.backend.dto.response.SubwayNoticeResponse;
import org.example.backend.exception.CustomException;
import org.example.backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubwayService {

    private final RestTemplate restTemplate;
    private final StationRepository stationRepository;
    private final NoticeRepository noticeRepository;
    private final ObjectMapper objectMapper;

    @Value("${seoul.api.key}")
    private String apiKey;
    @Value("${seoul.live.key}")
    private String liveApiKey;

    private static final String SEOUL_API_BASE_URL = "http://openapi.seoul.go.kr:8088";

    @Transactional
    public void updateStationDatabase() {
        String url = SEOUL_API_BASE_URL + "/" + apiKey + "/json/SearchSTNBySubwayLineInfo/1/1000/";

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

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

            if (response.getErrorMessage() != null && !"INFO-000".equals(response.getErrorMessage().getCode())) {
                log.warn("API returned a non-successful code for station {}: {}", stationName, response.getErrorMessage().getCode());
                if ("INFO-200".equals(response.getErrorMessage().getCode())) {
                    return Collections.emptyList();
                }
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

    @Transactional
    public void fetchAndSaveNotices(int pageNo, int numOfRows) {
        String serviceName = "getNtceList";
        String url = String.join("/", SEOUL_API_BASE_URL, apiKey, "json", serviceName, String.valueOf(pageNo), String.valueOf(numOfRows));

        try {
            log.info("Calling Seoul Open API: {}", url);
            String rawResponse = restTemplate.getForObject(url, String.class);
            log.info("Raw response from Subway Notice API: {}", rawResponse);

            if (rawResponse == null || rawResponse.trim().isEmpty()) {
                log.error("Subway Notice API returned an empty response.");
                throw new CustomException(ErrorCode.API_RESULT_ERROR, "API 응답이 비어있습니다.");
            }

            SubwayNoticeResponse response = objectMapper.readValue(rawResponse, SubwayNoticeResponse.class);

            if (response != null && response.getResponse() != null && response.getResponse().getHeader() != null && "00".equals(response.getResponse().getHeader().getResultCode())) {
                List<SubwayNoticeResponse.NoticeItem> items = response.getResponse().getBody().getItems().getItem();
                if (items != null) {
                    for (SubwayNoticeResponse.NoticeItem item : items) {
                        noticeRepository.findByNoticeTitleAndStartDate(item.getNoticeTitle(), item.getStartDate())
                                .orElseGet(() -> {
                                    Notice notice = Notice.builder()
                                            .noticeTitle(item.getNoticeTitle())
                                            .noticeContent(item.getNoticeContent())
                                            .lineList(item.getLineList())
                                            .incidentType(IncidentType.fromString(item.getNoticeTitle()))
                                            .startDate(item.getStartDate())
                                            .endDate(item.getEndDate())
                                            .stationCodeList(item.getStationCodeList())
                                            .nonstopYn(item.getNonstopYn())
                                            .noticeCode(item.getNoticeCode())
                                            .createdDate(item.getCreatedDate())
                                            .occurredDate(item.getOccurredDate())
                                            .upDownBound(item.getUpDownBound())
                                            .build();
                                    return noticeRepository.save(notice);
                                });
                    }
                }
            } else {
                String errorCode = (response != null && response.getResponse() != null && response.getResponse().getHeader() != null) ? response.getResponse().getHeader().getResultCode() : "UNKNOWN_CODE";
                String errorMessage = (response != null && response.getResponse() != null && response.getResponse().getHeader() != null) ? response.getResponse().getHeader().getResultMsg() : "Unknown API error or malformed response.";
                log.error("Subway Notice API returned an error. Code: {}, Message: {}", errorCode, errorMessage);

                if (!"INFO-200".equals(errorCode)) {
                    throw new CustomException(ErrorCode.API_RESULT_ERROR, errorMessage);
                }
            }
        } catch (RestClientException e) {
            log.error("Subway Notice API call failed: {}", e.getMessage());
            throw new CustomException(ErrorCode.API_CALL_FAILED);
        } catch (Exception e) {
            log.error("An unexpected error occurred in fetchAndSaveNotices: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "지하철 알림 정보 조회 및 저장 중 오류 발생");
        }
    }

    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    public void scheduleNoticeUpdates() {
        log.info("Fetching and saving subway notices...");
        fetchAndSaveNotices(1, 50); // 우선 최근 50개만 가져오도록 설정
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesFromDB(String line, String incidentType, String stationName) {
        Specification<Notice> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (line != null && !line.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("lineList"), "%" + line + "%"));
            }

            if (incidentType != null && !incidentType.isEmpty()) {
                try {
                    IncidentType type = IncidentType.valueOf(incidentType.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("incidentType"), type));
                } catch (IllegalArgumentException e) {
                    return criteriaBuilder.disjunction();
                }
            }

            if (stationName != null && !stationName.isEmpty()) {
                List<Station> stations = stationRepository.findByNameContaining(stationName);
                if (stations.isEmpty()) {
                    return criteriaBuilder.disjunction();
                }
                Predicate stationPredicate = criteriaBuilder.disjunction();
                for (Station station : stations) {
                    stationPredicate = criteriaBuilder.or(stationPredicate,
                            criteriaBuilder.like(root.get("stationCodeList"), "%" + station.getStationCode() + "%"));
                }
                predicates.add(stationPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return noticeRepository.findAll(spec);
    }
}
