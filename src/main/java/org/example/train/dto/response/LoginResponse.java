package org.example.train.dto.response;

import org.example.train.model.enums.Role;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String name,
        String email,
        Role role,
        String token
) {}