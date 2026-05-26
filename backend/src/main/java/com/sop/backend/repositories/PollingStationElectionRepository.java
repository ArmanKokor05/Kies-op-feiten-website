package com.sop.backend.repositories;

import com.sop.backend.models.PollingStationElection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface PollingStationElectionRepository
        extends JpaRepository<PollingStationElection, Long> {

    boolean existsByPollingStation_NameAndElection_Name(String stationName, String electionName);

    Optional<PollingStationElection> findByPollingStation_NameAndElection_Name(String pollingStationName, String electionName);
    Page<PollingStationElection> findByElectionId(Long electionId, Pageable pageable);
}
