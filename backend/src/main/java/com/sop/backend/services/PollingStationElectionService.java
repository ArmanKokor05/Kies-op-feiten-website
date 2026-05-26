package com.sop.backend.services;

import com.sop.backend.models.Election2;
import com.sop.backend.models.PollingStation;
import com.sop.backend.models.PollingStationElection;
import com.sop.backend.repositories.PollingStationElectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class PollingStationElectionService implements ServiceInterface<PollingStationElection> {

    private final PollingStationElectionRepository repository;

    public PollingStationElectionService(PollingStationElectionRepository repository) {
        this.repository = repository;
    }

    public PollingStationElection create(PollingStation pollingStation, Election2 election) {
        PollingStationElection pollingStationElection = new PollingStationElection();
        pollingStationElection.setPollingStation(pollingStation);
        pollingStationElection.setElection(election);

        return pollingStationElection;
    }

    public Page<PollingStationElection> findByElectionId(long electionId, Pageable pageable) {
        return repository.findByElectionId(electionId, pageable);
    }

    @Override
    public PollingStationElection save(PollingStationElection pollingStationElection) {
        String pollingStationName =  pollingStationElection.getPollingStation().getName();
        String electionName = pollingStationElection.getElection().getName();

        return repository.findByPollingStation_NameAndElection_Name(pollingStationName, electionName)
                .orElseGet(() -> repository.save(pollingStationElection));
    }
}
