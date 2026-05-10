package org.example.train.repository;

import org.example.train.model.Route;
import org.example.train.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

    Optional<Route> findByRouteNumber(String routeNumber);

    // find all routes that contain a given station
    @Query("SELECT r FROM Route r JOIN r.stations s WHERE s = :station")
    List<Route> findByStation(@Param("station") Station station);

    // find all routes that contain both stations — used for direct journey search
    @Query("SELECT r FROM Route r JOIN r.stations s1 JOIN r.stations s2 " +
            "WHERE s1 = :from AND s2 = :to")
    List<Route> findByFromStationAndToStation(
            @Param("from") Station from,
            @Param("to") Station to
    );

    boolean existsByRouteNumber(String routeNumber);
}