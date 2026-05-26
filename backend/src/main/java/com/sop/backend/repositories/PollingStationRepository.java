package com.sop.backend.repositories;

import com.sop.backend.models.PollingStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollingStationRepository extends JpaRepository<PollingStation, Long> {


    Optional<PollingStation> findByNameAndZipcode(String name, String zipcode);

    Page<PollingStation> findByMunicipalityIdAndPollingStationElectionsElectionId(
            long municipalityId,
            long electionId,
            Pageable pageable
    );
}
