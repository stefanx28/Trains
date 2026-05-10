package org.example.train.dto.response;

import java.util.List;
import java.util.UUID;

public record RouteResponse(
        UUID id,
        String routeNumber,
        List<StationResponse> stations
) {}