package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sop.backend.exceptions.PartyMapperException;
import com.sop.backend.models.Party2;
import com.sop.backend.xml.XmlElements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartyMapperTest {

    private PartyMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mapper = new PartyMapper(null) {
            public JsonNode getJsonNode(JsonNode jsonNode, XmlElements[] path) {
                return jsonNode.get("Affiliation");
            }
        };
    }

    @Test
    void assembleModel_shouldReturnPartiesWithAcronymAndName() throws Exception {
        String json = """
                {
                  "Affiliation": [
                    {
                      "AffiliationIdentifier": {
                        "Id": "1",
                        "RegisteredName": "PVV (Partij voor de Vrijheid)"
                      }
                    },
                    {
                      "AffiliationIdentifier": {
                        "Id": "2",
                        "RegisteredName": "VVD (Volkspartij voor Vrijheid en Democratie)"
                      }
                    }
                  ]
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<Party2> parties = mapper.assembleModel(rootNode);

        assertEquals(2, parties.size());

        Party2 first = parties.get(0);
        assertEquals("PVV", first.getAcronym());
        assertEquals("Partij voor de Vrijheid", first.getName());

        Party2 second = parties.get(1);
        assertEquals("VVD", second.getAcronym());
        assertEquals("Volkspartij voor Vrijheid en Democratie", second.getName());
    }

    @Test
    void assembleModel_shouldHandlePartyWithoutParentheses() throws Exception {
        String json = """
                {
                  "Affiliation": [
                    {
                      "AffiliationIdentifier": {
                        "Id": "3",
                        "RegisteredName": "PVV"
                      }
                    }
                  ]
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<Party2> parties = mapper.assembleModel(rootNode);

        assertEquals(1, parties.size());

        Party2 party = parties.get(0);
        assertEquals("PVV", party.getAcronym());
        assertEquals("", party.getName());
    }

    @Test
    void assembleModel_shouldThrowExceptionIfNodeIsNotArray() throws Exception {
        String json = """
                {
                  "Affiliation": {
                    "AffiliationIdentifier": {
                      "Id": "1",
                      "RegisteredName": "PVV (Partij voor de Vrijheid)"
                    }
                  }
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);

        assertThrows(PartyMapperException.class, () -> mapper.assembleModel(rootNode));
    }

    @Test
    void assembleCandidatePartyModel_shouldReturnSingleParty() throws Exception {
        String json = """
                {
                  "AffiliationIdentifier": {
                    "Id": "4",
                    "RegisteredName": "D66 (Democraten 66)"
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Party2 party = mapper.AssembleCandidatePartyModel(node);

        assertEquals("D66", party.getAcronym());
        assertEquals("Democraten 66", party.getName());
    }

    @Test
    void populateModel_shouldHandleExtraSpacesAroundParentheses() throws Exception {
        String json = """
                {
                  "RegisteredName": "PVV   (  Partij voor de Vrijheid  )"
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Party2 party = mapper.populateModel(node);

        assertEquals("PVV", party.getAcronym());
        assertEquals("Partij voor de Vrijheid", party.getName());
    }

    @Test
    void populateModel_shouldHandleComplexPartyNames() throws Exception {
        String json = """
                {
                  "RegisteredName": "CDA (Christen-Democratisch Appèl)"
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Party2 party = mapper.populateModel(node);

        assertEquals("CDA", party.getAcronym());
        assertEquals("Christen-Democratisch Appèl", party.getName());
    }
}