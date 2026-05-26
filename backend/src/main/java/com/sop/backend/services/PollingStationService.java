package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.dto.CreatePollingStationsDTO;
import com.sop.backend.models.*;
import com.sop.backend.repositories.PollingStationRepository;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.mappers.PollingStationMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PollingStationService implements ServiceInterface<PollingStation> {

    private final XmlToJsonParser xmlToJsonParser;
    private final PollingStationMapper pollingStationMapper;
    private final PollingStationRepository pollingStationRepository;
    private final ElectionService electionService;
    private final MunicipalityService municipalityService;
    private final PollingStationElectionService pollingStationElectionService;
    @PersistenceContext
    private EntityManager entityManager;

    public PollingStationService(XmlToJsonParser xmlToJsonParser,
                                 PollingStationMapper pollingStationMapper,
                                 PollingStationRepository pollingStationRepository,
                                 ElectionService electionService,
                                 MunicipalityService municipalityService,
                                 PollingStationElectionService pollingStationElectionService) {
        this.xmlToJsonParser = xmlToJsonParser;
        this.pollingStationMapper = pollingStationMapper;
        this.pollingStationRepository = pollingStationRepository;
        this.electionService = electionService;
        this.municipalityService = municipalityService;
        this.pollingStationElectionService = pollingStationElectionService;
    }

    @Transactional
    public CreatePollingStationsDTO createPollingStations(MultipartFile[] files) throws IOException {
        List<JsonNode> jsonNodes = this.xmlToJsonParser.parseMultipleFilesToJson(files);

        for  (JsonNode jsonNode : jsonNodes) {
            Election2 election = saveElection(jsonNode);
            Municipality municipality = saveMunicipality(jsonNode);

            List<PollingStation> pollingStations = pollingStationMapper.assembleModel(jsonNode);

            saveRelationalModels(pollingStations, election, municipality);
        }

        CreatePollingStationsDTO dto = new CreatePollingStationsDTO();

        // TODO: Set amount the correct way
        dto.setAmount(1);

        return dto;
    }

    private Election2 saveElection(JsonNode jsonNode) {
        Election2 election = electionService.parseFromJson(jsonNode);
        return electionService.save(election);
    }

    private Municipality saveMunicipality(JsonNode jsonNode) {
        Municipality municipality = municipalityService.parseFromJson(jsonNode);
        return municipalityService.save(municipality);
    }

    /**
     * Saves polling stations and their related election and municipality data
     * in a way that is semi-optimized for large imports.
     *
     * <p>
     * This method processes the polling stations one by one, but every 100 items
     * it forces pending database changes to be written and clears Hibernate's
     * internal memory.
     * </p>
     *
     * <p>
     * Clearing the internal memory prevents Hibernate from keeping thousands of
     * objects in memory at the same time, which would slow down the application
     * and increase memory usage during large imports.
     * </p>
     *
     * <p>
     * This approach keeps performance stable and memory usage low, even when
     * importing large XML files.
     * </p>
     */
    @Transactional
    protected void saveRelationalModels(List<PollingStation> pollingStations,
                                        Election2 election,
                                        Municipality municipality) {
        int flushCount = 0;

        for (PollingStation pollingStation : pollingStations) {
            pollingStation.setMunicipality(municipality);
            pollingStation = save(pollingStation);

            if (++flushCount % 100 == 0) {
                entityManager.flush();
                entityManager.clear();
            }

            savePollingStationElection(pollingStation, election);
        }
    }

    @Override
    public PollingStation save(PollingStation pollingStation) {
        Optional<PollingStation> existing = checkForExisting(pollingStation);
        return existing.orElseGet(() -> pollingStationRepository.save(pollingStation));
    }

    private Optional<PollingStation> checkForExisting(PollingStation pollingStation) {
        return pollingStationRepository.findByNameAndZipcode(
                pollingStation.getName(),
                pollingStation.getZipcode()
        );
    }

    public Page<PollingStation> findByElectionId(long electionId, Pageable pageable) {
        Page<PollingStationElection> page = pollingStationElectionService.findByElectionId(electionId, pageable);

        return page.map(PollingStationElection::getPollingStation);
    }

    private void savePollingStationElection(PollingStation pollingStation, Election2 election) {
        PollingStationElection pollingStationElection =
                pollingStationElectionService.create(pollingStation, election);

        pollingStationElectionService.save(pollingStationElection);
    }

    public Page<PollingStation> findByElectionAndMunicipality(long electionId, long municipalityId, Pageable pageable) {
        return pollingStationRepository
                .findByMunicipalityIdAndPollingStationElectionsElectionId(municipalityId, electionId, pageable);
    }
}
