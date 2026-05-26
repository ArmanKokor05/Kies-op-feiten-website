package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.dto.CreatePollingStationsDTO;
import com.sop.backend.models.*;
import com.sop.backend.repositories.PollingStationRepository;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.mappers.PollingStationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PollingStationServiceTest {

    @Mock
    private XmlToJsonParser xmlToJsonParser;

    @Mock
    private PollingStationMapper pollingStationMapper;

    @Mock
    private PollingStationRepository pollingStationRepository;

    @Mock
    private ElectionService electionService;

    @Mock
    private MunicipalityService municipalityService;

    @Mock
    private PollingStationElectionService pollingStationElectionService;

    @InjectMocks
    private PollingStationService pollingStationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPollingStations_shouldReturnDTO() throws IOException {
        MultipartFile[] files = new MultipartFile[0];
        JsonNode jsonNode = mock(JsonNode.class);
        Election2 election = new Election2();
        Municipality municipality = new Municipality();
        PollingStation pollingStation = new PollingStation();

        // Mock parser
        when(xmlToJsonParser.parseMultipleFilesToJson(files)).thenReturn(List.of(jsonNode));

        // Mock dependent services
        when(electionService.parseFromJson(jsonNode)).thenReturn(election);
        when(electionService.save(election)).thenReturn(election);

        when(municipalityService.parseFromJson(jsonNode)).thenReturn(municipality);
        when(municipalityService.save(municipality)).thenReturn(municipality);

        // Mock mapper
        when(pollingStationMapper.assembleModel(jsonNode)).thenReturn(List.of(pollingStation));

        // Mock repository save
        when(pollingStationRepository.findByNameAndZipcode(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(pollingStationRepository.save(pollingStation)).thenReturn(pollingStation);

        // Mock pollingStationElectionService
        PollingStationElection pse = new PollingStationElection();
        when(pollingStationElectionService.create(pollingStation, election)).thenReturn(pse);
        when(pollingStationElectionService.save(pse)).thenReturn(pse);

        CreatePollingStationsDTO dto = pollingStationService.createPollingStations(files);

        assertEquals(1, dto.getAmount());

        // Verify interactions
        verify(pollingStationMapper).assembleModel(jsonNode);
        verify(pollingStationRepository).save(pollingStation);
        verify(pollingStationElectionService).save(pse);
    }

    @Test
    void save_shouldReturnExistingPollingStation() {
        PollingStation existing = new PollingStation();
        existing.setName("Test");
        existing.setZipcode("1234 AB");

        PollingStation newStation = new PollingStation();
        newStation.setName("Test");
        newStation.setZipcode("1234 AB");

        when(pollingStationRepository.findByNameAndZipcode("Test", "1234 AB"))
                .thenReturn(Optional.of(existing));

        PollingStation result = pollingStationService.save(newStation);

        assertThat(result).isSameAs(existing);
        verify(pollingStationRepository, never()).save(any());
    }

    @Test
    void save_shouldSaveIfNotExisting() {
        PollingStation pollingStation = new PollingStation();
        pollingStation.setName("New");
        pollingStation.setZipcode("5678 CD");

        when(pollingStationRepository.findByNameAndZipcode("New", "5678 CD"))
                .thenReturn(Optional.empty());
        when(pollingStationRepository.save(pollingStation)).thenReturn(pollingStation);

        PollingStation result = pollingStationService.save(pollingStation);

        assertThat(result).isSameAs(pollingStation);
        verify(pollingStationRepository).save(pollingStation);
    }

    @Test
    void findByElectionId_shouldReturnPollingStations() {
        PollingStationElection pse = new PollingStationElection();
        PollingStation pollingStation = new PollingStation();
        pse.setPollingStation(pollingStation);

        Page<PollingStationElection> page = new PageImpl<>(List.of(pse));
        when(pollingStationElectionService.findByElectionId(1L, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<PollingStation> result = pollingStationService.findByElectionId(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0)).isSameAs(pollingStation);
    }

    @Test
    void findByElectionAndMunicipality_shouldCallRepository() {
        Page<PollingStation> page = new PageImpl<>(List.of(new PollingStation()));
        when(pollingStationRepository
                .findByMunicipalityIdAndPollingStationElectionsElectionId(2L, 1L, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<PollingStation> result = pollingStationService
                .findByElectionAndMunicipality(1L, 2L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(pollingStationRepository)
                .findByMunicipalityIdAndPollingStationElectionsElectionId(2L, 1L, PageRequest.of(0, 10));
    }
}
