package org.example.train.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StationRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Code is required")
        @Size(min = 2, max = 5, message = "Code must be between 2 and 5 characters")
        String code
) {}