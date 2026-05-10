package org.example.train.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TrainRequest(

        @NotBlank(message = "Train number is required")
        String trainNumber,

        @NotBlank(message = "Name is required")
        String name,

        @Min(value = 1, message = "Total seats must be at least 1")
        int totalSeats
) {}