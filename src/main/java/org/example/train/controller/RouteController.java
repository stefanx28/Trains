package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.RouteRequest;
import org.example.train.dto.response.RouteResponse;
import org.example.train.dto.response.StationResponse;
import org.example.train.mappers.RouteMapper;
import org.example.train.model.Route;
import org.example.train.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RouteController {

    private final RouteService routeService;
    private final RouteMapper routeMapper;

    @GetMapping
    public ResponseEntity<List<RouteResponse>> findAll() {
        List<RouteResponse> routes = routeService.findAll().stream()
                .map(routeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(routeMapper.toResponse(routeService.findById(id)));
    }

    // admin only
    @PostMapping
    public ResponseEntity<RouteResponse> add(@Valid @RequestBody RouteRequest request) {
        Route route = routeService.add(request.routeNumber(), request.stationIds());
        return ResponseEntity.ok(routeMapper.toResponse(route));
    }

    // admin only
    @PutMapping("/{id}")
    public ResponseEntity<RouteResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody RouteRequest request) {
        Route route = routeService.update(id, request.routeNumber(), request.stationIds());
        return ResponseEntity.ok(routeMapper.toResponse(route));
    }

    // admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}