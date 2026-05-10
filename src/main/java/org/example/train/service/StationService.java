package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Station;
import org.example.train.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public Station add(String name, String city, String code) {
        if (stationRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Station code already exists: " + code);
        }
        Station station = new Station();
        station.setName(name);
        station.setCity(city);
        station.setCode(code);
        return stationRepository.save(station);
    }

    public Station findById(UUID id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + id));
    }

    public Station findByCode(String code) {
        return stationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + code));
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station update(UUID id, String name, String city, String code) {
        Station station = findById(id);
        station.setName(name);
        station.setCity(city);
        station.setCode(code);
        return stationRepository.save(station);
    }

    public void delete(UUID id) {
        stationRepository.deleteById(id);
    }
}