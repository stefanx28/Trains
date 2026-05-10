package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.StationResponse;
import org.example.train.model.Station;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationMapper {
    public StationResponse toResponse(Station station) {
        return new StationResponse(station.getId(), station.getName(),
                station.getCity(), station.getCode());
    }
}
