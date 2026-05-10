package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.TrainResponse;
import org.example.train.model.Train;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainMapper {
    public TrainResponse toResponse(Train train) {
        return new TrainResponse(train.getId(), train.getTrainNumber(),
                train.getName(), train.getTotalSeats());
    }
}
