package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.ScheduleRequest;
import org.example.train.dto.response.RouteResponse;
import org.example.train.dto.response.ScheduleResponse;
import org.example.train.dto.response.StationResponse;
import org.example.train.dto.response.TrainResponse;
import org.example.train.mappers.ScheduleMapper;
import org.example.train.model.Schedule;
import org.example.train.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> findAll() {
        List<ScheduleResponse> schedules = scheduleService.findAll().stream()
                .map(scheduleMapper::toResponse)
                .toList();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleMapper.toResponse(scheduleService.findById(id)));
    }

    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<ScheduleResponse>> findByTrain(@PathVariable UUID trainId) {
        List<ScheduleResponse> schedules = scheduleService.findByTrain(trainId).stream()
                .map(scheduleMapper::toResponse)
                .toList();
        return ResponseEntity.ok(schedules);
    }


    @PostMapping
    public ResponseEntity<ScheduleResponse> add(@Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.add(request.trainId(), request.routeId(),
                request.departureTime(), request.arrivalTime());
        return ResponseEntity.ok(scheduleMapper.toResponse(schedule));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.update(id, request.trainId(), request.routeId(),
                request.departureTime(), request.arrivalTime());
        return ResponseEntity.ok(scheduleMapper.toResponse(schedule));
    }


    @PatchMapping("/{id}/delay")
    public ResponseEntity<ScheduleResponse> setDelay(@PathVariable UUID id,
                                                     @RequestParam int delayMinutes) {
        Schedule schedule = scheduleService.setDelay(id, delayMinutes);
        return ResponseEntity.ok(scheduleMapper.toResponse(schedule));
    }

    // admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}