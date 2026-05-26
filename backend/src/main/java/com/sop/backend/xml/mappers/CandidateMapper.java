package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.CandidateMapperException;
import com.sop.backend.exceptions.XmlTransformerException;
import com.sop.backend.models.Candidate;
import com.sop.backend.xml.XmlElements;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.paths.CandidatePaths;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CandidateMapper extends Mapper<Candidate> implements MapperInterface<Candidate>{

    public CandidateMapper(XmlToJsonParser xmlToJsonParser) {
        super(xmlToJsonParser);
    }

    @Override
    public List<Candidate> assembleModel(JsonNode jsonNode) {
        List<Candidate> candidates = new ArrayList<>();

        if (jsonNode.isArray()){
            throw new CandidateMapperException("affiliation node is an array");
        }

        JsonNode candidateNodes = getJsonNode(jsonNode, CandidatePaths.CANDIDATE_PATH);

        if (!candidateNodes.isArray()){
            Candidate candidate = populateModel(candidateNodes);
            candidates.add(candidate);

            return candidates;
        }

        for (JsonNode node :  candidateNodes) {
            Candidate candidate = populateModel(node);
            candidates.add(candidate);
        }

        return candidates;
    }

    @Override
    protected Candidate populateModel(JsonNode jsonNode) {
        JsonNode candidateGender =  jsonNode.path(XmlElements.GENDER.getValue());
        JsonNode candidateIdNode = jsonNode.path(XmlElements.CANDIDATE_IDENTIFIER.getValue());

        String qualifyingAddress = getQualifyingAddress(jsonNode);

        JsonNode candidateFullNameNode = jsonNode.path(XmlElements.CANDIDATE_FULL_NAME.getValue());
        JsonNode personNameNode = candidateFullNameNode.path(XmlElements.PERSON_NAME.getValue());

        JsonNode candidateFirstNameNode = personNameNode.path(XmlElements.FIRST_NAME.getValue());
        JsonNode candidatePrefixNode = personNameNode.path(XmlElements.LAST_NAME_PREFIX.getValue());
        JsonNode candidateLastNameNode = personNameNode.path(XmlElements.LAST_NAME.getValue());
        JsonNode candidateInitialsNode = personNameNode.path(XmlElements.INITIALS.getValue());

        Candidate candidate = new Candidate();

        String candidateLastName;

        candidateLastName = handleMissingData(candidateLastNameNode);


        if (!candidatePrefixNode.isMissingNode()) {
            String prefix = getNodeValue(candidatePrefixNode);
            if (prefix != null && !prefix.trim().isEmpty()) {
                candidateLastName = (prefix.trim() + " " + candidateLastName.trim());
            }
        }

        candidate.setFirstName(handleMissingData(candidateFirstNameNode));
        candidate.setLastName( candidateLastName);
        candidate.setInitials(handleMissingData(candidateInitialsNode));
        candidate.setGender(handleMissingData(candidateGender));
        candidate.setQualifyingAddress(qualifyingAddress);
        candidate.setCandidateId(getNodeIdAsInt(candidateIdNode));

        return candidate;
    }

    private String getQualifyingAddress(JsonNode jsonNode) {
        try {
            JsonNode localityNameNode = getJsonNode(jsonNode, CandidatePaths.QUALIFYING_ADDRESS_WITH_COUNTRY);
            return getNodeValue(localityNameNode);
        } catch (XmlTransformerException e) {
            try {
                JsonNode localityNameNode = getJsonNode(jsonNode, CandidatePaths.QUALIFYING_ADDRESS);
                return getNodeValue(localityNameNode);
            } catch (XmlTransformerException ex) {
                return null;
            }
        }
    }

    private String handleMissingData(JsonNode node) {
        if (node.isMissingNode()) { return ""; }
        return getNodeValue(node);
    }
}
