package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.dto.common.ApiResponse;
import org.example.backend.dto.response.RealtimeArrival;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Subway Real-time & Admin API", description = "지하철 실시간 정보 조회 및 데이터 관리 API")
public interface SubwayControllerDocs {

    @Operation(summary = "지하철 역 마스터 데이터 업데이트", description = "(관리자용) 서울시 공공 API를 호출하여 DB에 모든 지하철역 정보를 업데이트/저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "API 호출 또는 서버 내부 오류")
    })
    ResponseEntity<ApiResponse<Void>> updateStations();

    @Operation(summary = "특정 역 실시간 도착 정보 조회", description = "특정 역의 실시간 열차 도착 정보 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RealtimeArrival.Arrival.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseEntity<ApiResponse<List<RealtimeArrival.Arrival>>> getRealtimeArrivals(
            @Parameter(description = "조회할 지하철 역 이름", example = "시청") String stationName
    );
}
