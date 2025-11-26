package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.Notice;
import org.example.backend.dto.common.ApiResponse;
import org.example.backend.dto.response.RealtimeArrival;
import org.example.backend.service.SubwayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subway")
@RequiredArgsConstructor
public class SubwayController implements SubwayControllerDocs {

    private final SubwayService subwayService;

    @Override
    @PostMapping("/update-stations")
    public ResponseEntity<ApiResponse<Void>> updateStations() {
        subwayService.updateStationDatabase();
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @GetMapping("/arrivals/{stationName}")
    public ResponseEntity<ApiResponse<List<RealtimeArrival.Arrival>>> getRealtimeArrivals(@PathVariable String stationName) {
        List<RealtimeArrival.Arrival> arrivals = subwayService.getRealtimeArrivals(stationName);
        return ResponseEntity.ok(ApiResponse.success(arrivals));
    }

    @Override
    @GetMapping("/notices")
    public ResponseEntity<ApiResponse<List<Notice>>> getSubwayNotices(
            @RequestParam(required = false) String line,
            @RequestParam(required = false) String incidentType,
            @RequestParam(required = false) String stationName
    ) {
        List<Notice> notices = subwayService.getNoticesFromDB(line, incidentType, stationName);
        return ResponseEntity.ok(ApiResponse.success(notices));
    }
}
