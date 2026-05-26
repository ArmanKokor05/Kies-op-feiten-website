package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sop.backend.exceptions.CandidateMapperException;
import com.sop.backend.models.Candidate;
import com.sop.backend.xml.XmlElements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateMapperTest {

    private CandidateMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mapper = new CandidateMapper(null) {
            public JsonNode getJsonNode(JsonNode jsonNode, XmlElements[] path) {
                if (path.length == 1 && path[0] == XmlElements.CANDIDATE) {
                    return jsonNode.get("Candidate");
                }
                if (path.length == 4 && path[0] == XmlElements.QUALIFYING_ADDRESS && path[1] == XmlElements.COUNTRY) {
                    JsonNode qualifyingAddress = jsonNode.path("QualifyingAddress");
                    JsonNode country = qualifyingAddress.path("Country");
                    if (country.isMissingNode()) {
                        throw new com.sop.backend.exceptions.XmlTransformerException("Country node not found");
                    }
                    return country.path("Locality").path("LocalityName");
                }
                if (path.length == 3 && path[0] == XmlElements.QUALIFYING_ADDRESS && path[1] == XmlElements.LOCALITY) {
                    JsonNode qualifyingAddress = jsonNode.path("QualifyingAddress");
                    JsonNode locality = qualifyingAddress.path("Locality");
                    if (locality.isMissingNode()) {
                        throw new com.sop.backend.exceptions.XmlTransformerException("Locality node not found");
                    }
                    return locality.path("LocalityName");
                }
                return jsonNode;
            }
        };
    }

    @Test
    void assembleModel_shouldReturnMultipleCandidates() throws Exception {
        String json = """
                {
                  "Candidate": [
                    {
                      "CandidateIdentifier": {
                        "Id": "1"
                      },
                      "CandidateFullName": {
                        "PersonName": {
                          "NameLine": "G.",
                          "FirstName": "Geert",
                          "LastName": "Wilders"
                        }
                      },
                      "Gender": "male",
                      "QualifyingAddress": {
                        "Locality": {
                          "LocalityName": "'s-Gravenhage"
                        }
                      }
                    },
                    {
                      "CandidateIdentifier": {
                        "Id": "2"
                      },
                      "CandidateFullName": {
                        "PersonName": {
                          "NameLine": "T.S.M.",
                          "FirstName": "Sebastiaan",
                          "LastName": "Stöteler"
                        }
                      },
                      "Gender": "male",
                      "QualifyingAddress": {
                        "Locality": {
                          "LocalityName": "Almelo"
                        }
                      }
                    }
                  ]
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<Candidate> candidates = mapper.assembleModel(rootNode);

        assertEquals(2, candidates.size());

        Candidate first = candidates.get(0);
        assertEquals("Geert", first.getFirstName());
        assertEquals("Wilders", first.getLastName());
        assertEquals("G.", first.getInitials());
        assertEquals("male", first.getGender());
        assertEquals("'s-Gravenhage", first.getQualifyingAddress());
        assertEquals(1, first.getCandidateId());

        Candidate second = candidates.get(1);
        assertEquals("Sebastiaan", second.getFirstName());
        assertEquals("Stöteler", second.getLastName());
        assertEquals("T.S.M.", second.getInitials());
        assertEquals("male", second.getGender());
        assertEquals("Almelo", second.getQualifyingAddress());
        assertEquals(2, second.getCandidateId());
    }

    @Test
    void assembleModel_shouldHandleSingleCandidate() throws Exception {
        String json = """
                {
                  "Candidate": {
                    "CandidateIdentifier": {
                      "Id": "3"
                    },
                    "CandidateFullName": {
                      "PersonName": {
                        "NameLine": "J.M.M.",
                        "FirstName": "Shanna",
                        "LastName": "Schilder"
                      }
                    },
                    "Gender": "female",
                    "QualifyingAddress": {
                      "Locality": {
                        "LocalityName": "Volendam"
                      }
                    }
                  }
                }
                """;

        JsonNode rootNode = objectMapper.readTree(json);
        List<Candidate> candidates = mapper.assembleModel(rootNode);

        assertEquals(1, candidates.size());

        Candidate candidate = candidates.get(0);
        assertEquals("Shanna", candidate.getFirstName());
        assertEquals("Schilder", candidate.getLastName());
        assertEquals("J.M.M.", candidate.getInitials());
        assertEquals("female", candidate.getGender());
        assertEquals("Volendam", candidate.getQualifyingAddress());
        assertEquals(3, candidate.getCandidateId());
    }

    @Test
    void assembleModel_shouldThrowExceptionIfRootNodeIsArray() throws Exception {
        String json = """
                [
                  {
                    "Candidate": {
                      "CandidateIdentifier": {
                        "Id": "1"
                      }
                    }
                  }
                ]
                """;

        JsonNode rootNode = objectMapper.readTree(json);

        assertThrows(CandidateMapperException.class, () -> mapper.assembleModel(rootNode));
    }

    @Test
    void populateModel_shouldHandleLastNamePrefix() throws Exception {
        String json = """
                {
                  "CandidateIdentifier": {
                    "Id": "79"
                  },
                  "CandidateFullName": {
                    "PersonName": {
                      "NameLine": "A.W.J.A.",
                      "FirstName": "Alexander",
                      "LastNamePrefix": "van",
                      "LastName": "Hattem"
                    }
                  },
                  "Gender": "male",
                  "QualifyingAddress": {
                    "Locality": {
                      "LocalityName": "Vinkel"
                    }
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Candidate candidate = mapper.populateModel(node);

        assertEquals("Alexander", candidate.getFirstName());
        assertEquals("van Hattem", candidate.getLastName());
        assertEquals("A.W.J.A.", candidate.getInitials());
        assertEquals("male", candidate.getGender());
        assertEquals("Vinkel", candidate.getQualifyingAddress());
        assertEquals(79, candidate.getCandidateId());
    }

    @Test
    void populateModel_shouldHandleQualifyingAddressWithCountry() throws Exception {
        String json = """
                {
                  "CandidateIdentifier": {
                    "Id": "78"
                  },
                  "CandidateFullName": {
                    "PersonName": {
                      "NameLine": "M.",
                      "FirstName": "Marieke",
                      "LastName": "Ehlers"
                    }
                  },
                  "Gender": "female",
                  "QualifyingAddress": {
                    "Country": {
                      "CountryNameCode": "BE",
                      "Locality": {
                        "LocalityName": "Overijse"
                      }
                    }
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Candidate candidate = mapper.populateModel(node);

        assertEquals("Marieke", candidate.getFirstName());
        assertEquals("Ehlers", candidate.getLastName());
        assertEquals("M.", candidate.getInitials());
        assertEquals("female", candidate.getGender());
        assertEquals("Overijse", candidate.getQualifyingAddress());
        assertEquals(78, candidate.getCandidateId());
    }

    @Test
    void populateModel_shouldHandleMissingFields() throws Exception {
        String json = """
                {
                  "CandidateIdentifier": {
                    "Id": "100"
                  },
                  "CandidateFullName": {
                    "PersonName": {
                      "LastName": "Doe"
                    }
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Candidate candidate = mapper.populateModel(node);

        assertEquals("", candidate.getFirstName());
        assertEquals("Doe", candidate.getLastName());
        assertEquals("", candidate.getInitials());
        assertEquals("", candidate.getGender());
        assertNull(candidate.getQualifyingAddress());
        assertEquals(100, candidate.getCandidateId());
    }

    @Test
    void populateModel_shouldHandleEmptyLastNamePrefix() throws Exception {
        String json = """
                {
                  "CandidateIdentifier": {
                    "Id": "101"
                  },
                  "CandidateFullName": {
                    "PersonName": {
                      "NameLine": "J.",
                      "FirstName": "John",
                      "LastNamePrefix": "   ",
                      "LastName": "Smith"
                    }
                  },
                  "Gender": "male",
                  "QualifyingAddress": {
                    "Locality": {
                      "LocalityName": "Amsterdam"
                    }
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Candidate candidate = mapper.populateModel(node);

        assertEquals("John", candidate.getFirstName());
        assertEquals("Smith", candidate.getLastName());
        assertEquals("J.", candidate.getInitials());
    }

    @Test
    void populateModel_shouldTrimPrefixAndLastName() throws Exception {
        String json = """
                {
                  "CandidateIdentifier": {
                    "Id": "102"
                  },
                  "CandidateFullName": {
                    "PersonName": {
                      "NameLine": "P.",
                      "FirstName": "Peter",
                      "LastNamePrefix": "  van der  ",
                      "LastName": "  Berg  "
                    }
                  },
                  "Gender": "male",
                  "QualifyingAddress": {
                    "Locality": {
                      "LocalityName": "Utrecht"
                    }
                  }
                }
                """;

        JsonNode node = objectMapper.readTree(json);
        Candidate candidate = mapper.populateModel(node);

        assertEquals("Peter", candidate.getFirstName());
        assertEquals("van der Berg", candidate.getLastName());
        assertEquals("P.", candidate.getInitials());
    }
}