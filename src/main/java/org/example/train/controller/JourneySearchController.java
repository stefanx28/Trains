package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.config.NoJourneyFoundException;
import org.example.train.dto.request.JourneySearchRequest;
import org.example.train.dto.response.JourneyResponse;
import org.example.train.dto.response.RouteResponse;
import org.example.train.dto.response.ScheduleResponse;
import org.example.train.dto.response.StationResponse;
import org.example.train.dto.response.TrainResponse;
import org.example.train.mappers.ScheduleMapper;
import org.example.train.model.Schedule;
import org.example.train.service.JourneySearchService;
import org.example.train.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class JourneySearchController {

    private final JourneySearchService journeySearchService;
    private final ScheduleMapper scheduleMapper;

    @PostMapping("/search")
    public ResponseEntity<JourneyResponse> search(@Valid @RequestBody JourneySearchRequest request) {
        List<Schedule> direct = journeySearchService.findDirectJourneys(
                request.fromStationId(), request.toStationId(), request.after());

        List<List<Schedule>> changeover = journeySearchService.findChangeoverJourneys(
                request.fromStationId(), request.toStationId(), request.after());

        if (direct.isEmpty() && changeover.isEmpty()) {
            throw new NoJourneyFoundException(
                    "No journeys found between the selected stations on this date");
        }

        List<ScheduleResponse> directResponses = direct.stream()
                .map(scheduleMapper::toResponse)
                .toList();

        List<List<ScheduleResponse>> changeoverResponses = changeover.stream()
                .map(legs -> legs.stream()
                        .map(scheduleMapper::toResponse)
                        .toList())
                .toList();

        return ResponseEntity.ok(new JourneyResponse(directResponses, changeoverResponses));
    }
}