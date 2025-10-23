package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.StationResponse;
import org.example.backend.service.SubwayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.example.backend.dto.common.ApiResponse;



@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController implements StationControllerDocs {

    private final SubwayService subwayService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<StationResponse>>> getStations(
            @RequestParam(required = false) String line,
            @RequestParam(required = false) String name
    ) {
        List<StationResponse> stations = subwayService.getStations(line, name);
        return ResponseEntity.ok(ApiResponse.success(stations));
    }
}
