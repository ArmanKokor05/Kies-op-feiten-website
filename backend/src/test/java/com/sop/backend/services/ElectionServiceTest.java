package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.Election2;
import com.sop.backend.repositories.Election2Repository;
import com.sop.backend.xml.mappers.ElectionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElectionServiceTest {

    @Mock
    private ElectionMapper electionMapper;

    @Mock
    private Election2Repository election2Repository;

    @InjectMocks
    private ElectionService electionService;

    @Test
    void save_savesElection_whenNotExisting() {
        Election2 election = new Election2();
        election.setName("TK2025");

        when(election2Repository.findByName("TK2025"))
                .thenReturn(Optional.empty());
        when(election2Repository.save(election))
                .thenReturn(election);

        Election2 result = electionService.save(election);

        assertThat(result).isEqualTo(election);
        verify(election2Repository).save(election);
    }

    @Test
    void save_returnsExistingElection_whenAlreadyExists() {
        Election2 election = new Election2();
        election.setName("TK2025");

        when(election2Repository.findByName("TK2025"))
                .thenReturn(Optional.of(election));

        Election2 result = electionService.save(election);

        assertThat(result).isEqualTo(election);
        verify(election2Repository, never()).save(any());
    }

    @Test
    void parseFromJson_returnsElection_whenExactlyOneFound() {
        JsonNode jsonNode = mock(JsonNode.class);

        Election2 election = new Election2();
        election.setName("TK2025");

        when(electionMapper.assembleModel(jsonNode))
                .thenReturn(List.of(election));

        Election2 result = electionService.parseFromJson(jsonNode);

        assertThat(result).isEqualTo(election);
    }

    @Test
    void parseFromJson_throwsException_whenZeroElectionsFound() {
        JsonNode jsonNode = mock(JsonNode.class);

        when(electionMapper.assembleModel(jsonNode))
                .thenReturn(List.of());

        assertThatThrownBy(() -> electionService.parseFromJson(jsonNode))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected exactly one election");
    }

    @Test
    void parseFromJson_throwsException_whenMultipleElectionsFound() {
        JsonNode jsonNode = mock(JsonNode.class);

        Election2 election1 = new Election2();
        Election2 election2 = new Election2();

        when(electionMapper.assembleModel(jsonNode))
                .thenReturn(List.of(election1, election2));

        assertThatThrownBy(() -> electionService.parseFromJson(jsonNode))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected exactly one election");
    }
}
