package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sop.backend.models.PollingStation;
import com.sop.backend.xml.XmlElements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PollingStationMapperTest {

    private PollingStationMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mapper = new PollingStationMapper(null) {
            public JsonNode getJsonNode(JsonNode jsonNode, XmlElements[] path) {
                return jsonNode.get("PollingStations");
            }
        };
    }

    @Test
    void assembleModel_shouldReturnPollingStationsWithPostcodeAtEnd() throws Exception {
        String json = """
                {
                  "PollingStations": [
                    {
                      "ReportingUnitIdentifier": {
                        "Id": "1",
                        "": "Stembureau Central (postcode: 1234 AB)"
                      }
                    },
                    {
                      "ReportingUnitIdentifier": {
                        "Id": "2",
                        "": "Stembureau West (postcode: 5678 CD)"
                      }
                    }
                  ]
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<PollingStation> stations = mapper.assembleModel(rootNode);

        assertEquals(2, stations.size());

        PollingStation first = stations.get(0);
        assertEquals("Central", first.getName()); // Stembureau removed
        assertEquals("1234 AB", first.getZipcode());
        assertEquals("1", first.getIdentifier());

        PollingStation second = stations.get(1);
        assertEquals("West", second.getName());
        assertEquals("5678 CD", second.getZipcode());
        assertEquals("2", second.getIdentifier());
    }

    @Test
    void assembleModel_shouldHandleMissingPostcode() throws Exception {
        String json = """
                {
                  "PollingStations": [
                    {
                      "ReportingUnitIdentifier": {
                        "Id": "3",
                        "": "Stembureau East"
                      }
                    }
                  ]
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<PollingStation> stations = mapper.assembleModel(rootNode);

        assertEquals(1, stations.size());

        PollingStation station = stations.get(0);
        assertEquals("East", station.getName());
        assertEquals("-", station.getZipcode());
        assertEquals("3", station.getIdentifier());
    }

    @Test
    void assembleModel_shouldThrowExceptionIfNodeIsNotArray() throws Exception {
        String json = """
                {
                  "PollingStations": {
                    "ReportingUnitIdentifier": {
                      "Id": "4",
                      "": "Stembureau Central (postcode: 1234 AB)"
                    }
                  }
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);

        assertThrows(Exception.class, () -> mapper.assembleModel(rootNode));
    }

    @Test
    void populateModel_shouldTrimMultipleStembureauPrefixes() throws Exception {
        String json = """
                {
                  "ReportingUnitIdentifier": {
                    "Id": "5",
                    "": "Stembureau Stembureau North (postcode: 9999 ZZ)"
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        PollingStation station = mapper.populateModel(node);

        assertEquals("North", station.getName());
        assertEquals("9999 ZZ", station.getZipcode());
        assertEquals("5", station.getIdentifier());
    }
}
