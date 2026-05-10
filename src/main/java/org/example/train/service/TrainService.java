package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Train;
import org.example.train.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    public Train add(String trainNumber, String name, int totalSeats) {
        if (trainRepository.existsByTrainNumber(trainNumber)) {
            throw new IllegalArgumentException("Train number already exists: " + trainNumber);
        }
        Train train = new Train();
        train.setTrainNumber(trainNumber);
        train.setName(name);
        train.setTotalSeats(totalSeats);
        return trainRepository.save(train);
    }

    public Train findById(UUID id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Train not found: " + id));
    }

    public List<Train> findAll() {
        return trainRepository.findAll();
    }

    public Train update(UUID id, String trainNumber, String name, int totalSeats) {
        Train train = findById(id);
        train.setTrainNumber(trainNumber);
        train.setName(name);
        train.setTotalSeats(totalSeats);
        return trainRepository.save(train);
    }

    public void delete(UUID id) {
        trainRepository.deleteById(id);
    }
}