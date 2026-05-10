package org.example.train.repository;

import org.example.train.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {

    Optional<Station> findByCode(String code);

    Optional<Station> findByName(String name);

    boolean existsByCode(String code);
}