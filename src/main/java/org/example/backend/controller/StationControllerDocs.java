package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.backend.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.dto.response.StationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Station API", description = "지하철 역 마스터 정보 조회 API")
public interface StationControllerDocs {

    @Operation(summary = "지하철 역 목록 조회", description = "전체, 호선별, 이름으로 지하철 역 목록을 조회합니다. 파라미터를 모두 비우면 전체 역 목록이 반환됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StationResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseEntity<ApiResponse<List<StationResponse>>> getStations(
            @Parameter(description = "조회할 호선 이름 (예: 1호선, 2호선)", example = "2호선") String line,
            @Parameter(description = "검색할 역 이름 (부분 일치)", example = "강남") String name
    );
}
