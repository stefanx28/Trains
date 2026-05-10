package org.example.train.dto.response;

import org.example.train.model.Schedule;

import java.util.List;

public record JourneyResponse(
        List<ScheduleResponse> directJourneys,
        List<List<ScheduleResponse>> changeoverJourneys
) {}