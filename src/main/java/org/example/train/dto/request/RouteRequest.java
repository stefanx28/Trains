package org.example.train.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record RouteRequest(

        @NotBlank(message = "Route number is required")
        String routeNumber,

        @NotEmpty(message = "At least two stations are required")
        List<UUID> stationIds
) {}