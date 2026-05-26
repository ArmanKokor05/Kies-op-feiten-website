package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.Party2Repository;
import com.sop.backend.xml.mappers.PartyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartyServiceTest {

    private PartyMapper partyMapper;
    private Party2Repository party2Repository;
    private PartyService partyService;

    @BeforeEach
    void setUp() {
        partyMapper = mock(PartyMapper.class);
        party2Repository = mock(Party2Repository.class);
        partyService = new PartyService(partyMapper, party2Repository);
    }

    @Test
    void getParties_shouldReturnAllParties() {
        Party2 party1 = new Party2();
        Party2 party2 = new Party2();
        when(party2Repository.findAll()).thenReturn(List.of(party1, party2));

        List<Party2> parties = partyService.getParties();

        assertEquals(2, parties.size());
        verify(party2Repository, times(1)).findAll();
    }

    @Test
    void save_shouldReturnExistingParty_ifPresent() {
        Party2 existingParty = new Party2();
        existingParty.setAcronym("ABC");

        when(party2Repository.findByAcronym("ABC")).thenReturn(Optional.of(existingParty));

        Party2 result = partyService.save(existingParty);

        assertSame(existingParty, result);
        verify(party2Repository, never()).save(any());
    }

    @Test
    void save_shouldSaveParty_ifNotPresent() {
        Party2 newParty = new Party2();
        newParty.setAcronym("XYZ");

        when(party2Repository.findByAcronym("XYZ")).thenReturn(Optional.empty());
        when(party2Repository.save(newParty)).thenReturn(newParty);

        Party2 result = partyService.save(newParty);

        assertSame(newParty, result);
        verify(party2Repository, times(1)).save(newParty);
    }

    @Test
    void getAffiliationNodes_shouldDelegateToMapper() {
        JsonNode mockNode = mock(JsonNode.class);
        List<JsonNode> expectedNodes = List.of(mock(JsonNode.class), mock(JsonNode.class));

        when(partyMapper.getAffiliationNodes(mockNode)).thenReturn(expectedNodes);

        List<JsonNode> nodes = partyService.getAffiliationNodes(mockNode);

        assertEquals(expectedNodes, nodes);
        verify(partyMapper, times(1)).getAffiliationNodes(mockNode);
    }

    @Test
    void getCandidateParty_shouldDelegateToMapper() {
        JsonNode mockNode = mock(JsonNode.class);
        Party2 expectedParty = new Party2();

        when(partyMapper.AssembleCandidatePartyModel(mockNode)).thenReturn(expectedParty);

        Party2 result = partyService.getCandidateParty(mockNode);

        assertSame(expectedParty, result);
        verify(partyMapper, times(1)).AssembleCandidatePartyModel(mockNode);
    }
}
