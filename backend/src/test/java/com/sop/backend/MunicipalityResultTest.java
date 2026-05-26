package com.sop.backend;

import com.sop.backend.models.Election;
import com.sop.backend.models.PartyResult;
import com.sop.backend.repositories.PartyResultRepository;
import com.sop.backend.services.PartyColorService;
import com.sop.backend.services.PartyResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MunicipalityResultTest {

    @Mock
    private PartyResultRepository partyResultRepository;

    @Mock
    private PartyColorService partyColorService;

    private PartyResultService partyResultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        partyResultService = new PartyResultService(partyResultRepository, partyColorService);

        when(partyColorService.getPartyColor(anyString())).thenAnswer(invocation -> {
            String partyName = invocation.getArgument(0);
            return "#FF0000";
        });
    }

    @Test
    void testGetAvailableMunicipalities_ReturnsAll() {
        Election e1 = createElection(1L, 2023, "Amsterdam", "Noord-Holland", "alle");
        Election e2 = createElection(2L, 2023, "Rotterdam", "Zuid-Holland", "alle");
        Election e3 = createElection(3L, 2023, "Utrecht", "Utrecht", "alle");
        Election e4 = createElection(4L, 2023, "Den Haag", "Zuid-Holland", "alle");

        PartyResult p1 = createPartyResult(1L, "PVV", 1000, e1);
        PartyResult p2 = createPartyResult(2L, "VVD", 1000, e2);
        PartyResult p3 = createPartyResult(3L, "D66", 1000, e3);
        PartyResult p4 = createPartyResult(4L, "CDA", 1000, e4);

        when(partyResultRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3, p4));

        List<String> municipalities = partyResultService.getAvailableMunicipalities();

        assertNotNull(municipalities);
        assertEquals(4, municipalities.size());
        assertTrue(municipalities.contains("Amsterdam"));
        assertTrue(municipalities.contains("Rotterdam"));
        assertTrue(municipalities.contains("Utrecht"));
        assertTrue(municipalities.contains("Den Haag"));
    }

    private Election createElection(Long id, int year, String municipality, String region, String regionId) {
        Election election = new Election();
        election.setId(id);
        election.setElectionYear(year);
        election.setMunicipality(municipality);
        election.setRegion(region);
        election.setRegionId(regionId);
        return election;
    }

    private PartyResult createPartyResult(Long id, String partyName, int votes, Election election) {
        PartyResult result = new PartyResult();
        result.setId(id);
        result.setPartyId(partyName.toLowerCase());
        result.setPartyName(partyName);
        result.setValidVotes(votes);
        result.setElection(election);
        return result;
    }
}