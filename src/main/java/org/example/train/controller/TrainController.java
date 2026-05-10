package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.TrainRequest;
import org.example.train.dto.response.TrainResponse;
import org.example.train.mappers.TrainMapper;
import org.example.train.model.Train;
import org.example.train.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TrainController {

    private final TrainService trainService;
    private final TrainMapper trainMapper;

    @GetMapping
    public ResponseEntity<List<TrainResponse>> findAll() {
        List<TrainResponse> trains = trainService.findAll().stream()
                .map(trainMapper::toResponse)
                .toList();
        return ResponseEntity.ok(trains);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(trainMapper.toResponse(trainService.findById(id)));
    }

    // admin only
    @PostMapping
    public ResponseEntity<TrainResponse> add(@Valid @RequestBody TrainRequest request) {
        Train train = trainService.add(request.trainNumber(), request.name(), request.totalSeats());
        return ResponseEntity.ok(trainMapper.toResponse(train));
    }

    // admin only
    @PutMapping("/{id}")
    public ResponseEntity<TrainResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody TrainRequest request) {
        Train train = trainService.update(id, request.trainNumber(),
                request.name(), request.totalSeats());
        return ResponseEntity.ok(trainMapper.toResponse(train));
    }

    // admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        trainService.delete(id);
        return ResponseEntity.noContent().build();
    }


}