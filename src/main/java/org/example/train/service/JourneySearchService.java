package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.config.NoJourneyFoundException;
import org.example.train.dto.response.JourneyResponse;
import org.example.train.dto.response.ScheduleResponse;
import org.example.train.mappers.ScheduleMapper;
import org.example.train.model.Route;
import org.example.train.model.Schedule;
import org.example.train.model.Station;
import org.example.train.repository.RouteRepository;
import org.example.train.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JourneySearchService {

    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationService stationService;
    private final ScheduleMapper scheduleMapper;

    // return all direct schedules where the route contains both stations in the correct order
    public List<Schedule> findDirectJourneys(UUID fromId, UUID toId, LocalDateTime after) {
        Station from = stationService.findById(fromId);
        Station to = stationService.findById(toId);

        List<Route> directRoutes = routeRepository.findByFromStationAndToStation(from, to)
                .stream()
                .filter(route -> isCorrectOrder(route, from, to))
                .toList();

        return directRoutes.stream()
                .flatMap(route -> scheduleRepository
                        .findByRouteAndDepartureTimeAfter(route, after).stream())
                .toList();
    }

    // returns pairs of schedules [leg1, leg2] that connect via an intermediate station
    public List<List<Schedule>> findChangeoverJourneys(UUID fromId, UUID toId, LocalDateTime after) {
        Station from = stationService.findById(fromId);
        Station to = stationService.findById(toId);

        List<Route> routesFromOrigin = routeRepository.findByStation(from);
        List<Route> routesToDestination = routeRepository.findByStation(to);

        List<List<Schedule>> results = new ArrayList<>();

        for (Route leg1Route : routesFromOrigin) {
            for (Route leg2Route : routesToDestination) {
                Station intermediate = findCommonStation(leg1Route, leg2Route, from, to);
                if (intermediate == null) continue;

                List<Schedule> leg1Schedules = scheduleRepository
                        .findByRouteAndDepartureTimeAfter(leg1Route, after);

                for (Schedule leg1 : leg1Schedules) {
                    // leg2 must depart after leg1 arrives at the intermediate station
                    List<Schedule> leg2Schedules = scheduleRepository
                            .findByRouteAndDepartureTimeAfter(leg2Route, leg1.getArrivalTime());

                    for (Schedule leg2 : leg2Schedules) {
                        results.add(List.of(leg1, leg2));
                    }
                }
            }
        }
        return results;
    }

    // checks that from appears before to in the route's station list
    private boolean isCorrectOrder(Route route, Station from, Station to) {
        List<Station> stations = route.getStations();
        return stations.indexOf(from) < stations.indexOf(to);
    }

    // finds a station that appears in both routes, excluding origin and destination
    private Station findCommonStation(Route leg1Route, Route leg2Route,
                                      Station origin, Station destination) {
        return leg1Route.getStations().stream()
                .filter(s -> !s.equals(origin) && !s.equals(destination))
                .filter(leg2Route.getStations()::contains)
                .findFirst()
                .orElse(null);
    }
}