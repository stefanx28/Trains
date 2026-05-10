package org.example.train.dto.response;

import org.example.train.model.enums.Role;

import java.util.UUID;

public record PersonResponse(
        UUID id,
        String name,
        String email,
        Role role
) {}