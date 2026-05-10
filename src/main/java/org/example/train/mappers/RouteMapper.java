package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.RouteResponse;
import org.example.train.dto.response.StationResponse;
import org.example.train.model.Route;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RouteMapper {
    public RouteResponse toResponse(Route route) {
        List<StationResponse> stations = route.getStations().stream()
                .map(s -> new StationResponse(s.getId(), s.getName(), s.getCity(), s.getCode()))
                .toList();
        return new RouteResponse(route.getId(), route.getRouteNumber(), stations);
    }
}
