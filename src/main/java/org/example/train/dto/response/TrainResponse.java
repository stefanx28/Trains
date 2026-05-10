package org.example.train.dto.response;

import java.util.UUID;

public record TrainResponse(
        UUID id,
        String trainNumber,
        String name,
        int totalSeats
) {}