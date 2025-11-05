package org.example.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationCode(String stationCode);

    List<Station> findByLineNumber(String lineNumber);

    List<Station> findByNameContaining(String name);

    List<Station> findByLineNumberAndNameContaining(String lineNumber, String name);
}
