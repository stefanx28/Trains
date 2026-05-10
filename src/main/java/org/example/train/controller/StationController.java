package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.StationRequest;
import org.example.train.dto.response.StationResponse;
import org.example.train.mappers.StationMapper;
import org.example.train.model.Station;
import org.example.train.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class StationController {

    private final StationService stationService;
    private final StationMapper stationMapper;

    @GetMapping
    public ResponseEntity<List<StationResponse>> findAll() {
        List<StationResponse> stations = stationService.findAll().stream()
                .map(stationMapper::toResponse)
                .toList();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(stationMapper.toResponse(stationService.findById(id)));
    }


    @PostMapping
    public ResponseEntity<StationResponse> add(@Valid @RequestBody StationRequest request) {
        Station station = stationService.add(request.name(), request.city(), request.code());
        return ResponseEntity.ok(stationMapper.toResponse(station));
    }


    @PutMapping("/{id}")
    public ResponseEntity<StationResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody StationRequest request) {
        Station station = stationService.update(id, request.name(), request.city(), request.code());
        return ResponseEntity.ok(stationMapper.toResponse(station));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }


}