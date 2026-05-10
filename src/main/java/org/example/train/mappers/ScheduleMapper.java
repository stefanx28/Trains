package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.RouteResponse;
import org.example.train.dto.response.ScheduleResponse;
import org.example.train.dto.response.StationResponse;
import org.example.train.dto.response.TrainResponse;
import org.example.train.model.Schedule;
import org.example.train.service.ScheduleService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleMapper {
    private final ScheduleService service;

    public ScheduleResponse toResponse(Schedule schedule){
        TrainResponse trainResponse = new TrainResponse(schedule.getTrain().getId(), schedule.getTrain().getTrainNumber(), schedule.getTrain().getName(), schedule.getTrain().getTotalSeats());

        List<StationResponse> stations = schedule.getRoute().getStations().stream()
                .map(s -> new StationResponse(s.getId(), s.getName(), s.getCity(), s.getCode()))
                .toList();

        RouteResponse routeResponse = new RouteResponse(schedule.getRoute().getId(), schedule.getRoute().getRouteNumber(), stations);

        int seats = service.getAvailableSeats(schedule.getId());

        return new ScheduleResponse(schedule.getId(), trainResponse, routeResponse, schedule.getDepartureTime(), schedule.getArrivalTime(), schedule.getDelayMinutes(), schedule.getStatus(), seats);
    }

}
