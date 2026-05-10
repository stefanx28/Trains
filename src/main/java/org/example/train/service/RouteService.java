package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Route;
import org.example.train.model.Station;
import org.example.train.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final StationService stationService;

    public Route add(String routeNumber, List<UUID> stationIds) {
        if (routeRepository.existsByRouteNumber(routeNumber)) {
            throw new IllegalArgumentException("Route number already exists: " + routeNumber);
        }
        List<Station> stations = stationIds.stream()
                .map(stationService::findById)
                .toList();

        Route route = new Route();
        route.setRouteNumber(routeNumber);
        route.setStations(stations);
        return routeRepository.save(route);
    }

    public Route findById(UUID id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + id));
    }

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Route update(UUID id, String routeNumber, List<UUID> stationIds) {
        Route route = findById(id);
        List<Station> stations = stationIds.stream()
                .map(stationService::findById)
                .toList();
        route.setRouteNumber(routeNumber);
        route.setStations(stations);
        return routeRepository.save(route);
    }

    public void delete(UUID id) {
        routeRepository.deleteById(id);
    }
}