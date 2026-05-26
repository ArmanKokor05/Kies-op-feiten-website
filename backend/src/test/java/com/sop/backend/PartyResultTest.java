package com.sop.backend;

import com.sop.backend.models.Election;
import com.sop.backend.models.PartyResult;
import com.sop.backend.repositories.PartyResultRepository;
import com.sop.backend.services.PartyResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyResultServiceTest {

    @Mock
    private PartyResultRepository repository;

    @InjectMocks
    private PartyResultService service;

    private Election nationaleElection;

    @BeforeEach
    void setUp() {
        nationaleElection = new Election();
        nationaleElection.setRegionId("alle");
        nationaleElection.setElectionDate(LocalDate.of(2023, 11, 22));
        nationaleElection.setElectionYear(2023);
    }

    @Test
    void getSeatsByYear_BerekentZetelsMetDHondt() {
        List<PartyResult> partijen = Arrays.asList(
                maakParty("PVV", 2345473),
                maakParty("VVD", 2093977),
                maakParty("GL-PvdA", 1530707)
        );
        when(repository.findByElection_ElectionYear(2023)).thenReturn(partijen);

        List<PartyResult> result = service.getSeatsByYear(2023);

        assertNotNull(result);
        assertEquals(3, result.size());

        int totaal = result.stream().mapToInt(p -> p.getSeats()).sum();
        assertEquals(150, totaal);

        verify(repository).findByElection_ElectionYear(2023);
    }

    @Test
    void getSeatsByYear_SorteertOpZetels() {
        List<PartyResult> partijen = Arrays.asList(
                maakParty("Klein", 500000),
                maakParty("Groot", 2000000)
        );
        when(repository.findByElection_ElectionYear(2023)).thenReturn(partijen);

        List<PartyResult> result = service.getSeatsByYear(2023);

        assertEquals("Groot", result.get(0).getPartyName());
        assertTrue(result.get(0).getSeats() > result.get(1).getSeats());
    }

    @Test
    void getSeatsByYear_FiltertAlleenNationaal() {
        Election regionaal = new Election();
        regionaal.setRegionId("amsterdam");
        regionaal.setElectionDate(LocalDate.of(2023, 11, 22));
        regionaal.setElectionYear(2023);

        PartyResult p1 = maakParty("PVV", 2000000);
        PartyResult p2 = maakParty("VVD", 1000000);
        p2.setElection(regionaal);

        when(repository.findByElection_ElectionYear(2023)).thenReturn(Arrays.asList(p1, p2));

        List<PartyResult> result = service.getSeatsByYear(2023);

        assertEquals(1, result.size());
        assertEquals("PVV", result.get(0).getPartyName());
    }

    @Test
    void getSeatsByYear_GeenData_GeeftLegeLijst() {
        when(repository.findByElection_ElectionYear(1999)).thenReturn(Collections.emptyList());

        List<PartyResult> result = service.getSeatsByYear(1999);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSeatsByYear_NullStemmen_Krijgt0Zetels() {
        PartyResult p1 = maakParty("Partij A", 1000000);
        PartyResult p2 = maakParty("Partij B", null);

        when(repository.findByElection_ElectionYear(2023)).thenReturn(Arrays.asList(p1, p2));

        List<PartyResult> result = service.getSeatsByYear(2023);

        PartyResult partyB = result.stream()
                .filter(p -> "Partij B".equals(p.getPartyName()))
                .findFirst().get();

        assertEquals(0, partyB.getSeats());
    }

    @Test
    void getAvailableYears_RetourneertJaren() {
        List<PartyResult> allResults = Arrays.asList(
                maakPartyMetJaar("PVV", 1000000, 2023),
                maakPartyMetJaar("VVD", 1000000, 2021),
                maakPartyMetJaar("CDA", 1000000, 2017)
        );
        when(repository.findAll()).thenReturn(allResults);

        List<Integer> result = service.getAvailableYears();

        assertEquals(3, result.size());
        assertTrue(result.contains(2023));
        assertTrue(result.contains(2021));
        assertTrue(result.contains(2017));
        assertEquals(2023, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void getAvailableYears_GeenData_GeeftLegeLijst() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Integer> result = service.getAvailableYears();

        assertTrue(result.isEmpty());
    }

    private PartyResult maakParty(String naam, Integer stemmen) {
        PartyResult p = new PartyResult();
        p.setPartyName(naam);
        p.setPartyId(naam.toLowerCase());
        p.setValidVotes(stemmen);
        p.setElection(nationaleElection);
        return p;
    }

    private PartyResult maakPartyMetJaar(String naam, Integer stemmen, int jaar) {
        Election election = new Election();
        election.setRegionId("alle");
        election.setElectionDate(LocalDate.of(jaar, 11, 22));
        election.setElectionYear(jaar);

        PartyResult p = new PartyResult();
        p.setPartyName(naam);
        p.setPartyId(naam.toLowerCase());
        p.setValidVotes(stemmen);
        p.setElection(election);
        return p;
    }
}