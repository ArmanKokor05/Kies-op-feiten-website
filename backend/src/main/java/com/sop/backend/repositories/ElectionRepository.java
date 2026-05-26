package com.sop.backend.repositories;

import com.sop.backend.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    Optional<Election> findByElectionIdAndRegion(String electionId, String region);
    Optional<Election> findByElectionIdAndRegionAndMunicipalityId(String electionId, String region, String municipalityId);
}