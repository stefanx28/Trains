package org.example.train.dto.response;

import java.util.UUID;

public record StationResponse(
        UUID id,
        String name,
        String city,
        String code
) {}