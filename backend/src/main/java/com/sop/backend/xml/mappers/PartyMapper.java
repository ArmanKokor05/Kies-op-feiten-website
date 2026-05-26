package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.PartyMapperException;
import com.sop.backend.models.Party2;
import com.sop.backend.xml.XmlElements;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.paths.PartyPaths;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PartyMapper extends Mapper<Party2> implements MapperInterface<Party2> {

    public PartyMapper(XmlToJsonParser xmlToJsonParser) {
        super(xmlToJsonParser);
    }

    public List<JsonNode> getAffiliationNodes(JsonNode jsonNode) {
        List<JsonNode> affiliations = new ArrayList<>();
        JsonNode affiliationNode = getJsonNode(jsonNode, PartyPaths.BASE_PARTY_PATH);

        for (JsonNode affiliation : affiliationNode) {
            affiliations.add(affiliation);
        }

        return affiliations;
    }

    public Party2 AssembleCandidatePartyModel(JsonNode jsonNode) {
        return handleAffiliation(jsonNode);
    }

    public List<Party2> assembleModel(JsonNode jsonNode) {
        List<Party2> parties = new ArrayList<>();

        JsonNode affiliationNode = getJsonNode(jsonNode, PartyPaths.BASE_PARTY_PATH);



        if (!affiliationNode.isArray()) {
            throw new PartyMapperException("affiliation node is not an array");
        }

        for (JsonNode node : affiliationNode) {

            parties.add(handleAffiliation(node));
        }

        return parties;
    }

    private Party2 handleAffiliation(JsonNode node) {
        JsonNode partyNode = node.path(XmlElements.AFFILIATION_IDENTIFIER.getValue());
        return populateModel(partyNode);
    }

    @Override
    protected Party2 populateModel(JsonNode jsonNode) {
        Party2 party = new Party2();
        JsonNode partyNode = jsonNode.path(XmlElements.AFFILIATION_ACRONYM.getValue());
        String fullText = getNodeValue(partyNode);

        String[] result = extractAcronymAndName(fullText);

        party.setAcronym(result[0]);
        party.setName(result[1]);

        return party;
    }

    private String[] extractAcronymAndName(String fullText) {
        Pattern pattern = Pattern.compile("^(.+?)\\s*\\((.+?)\\)$");
        Matcher matcher = pattern.matcher(fullText);

        if (!matcher.find()) {
            return new String[]{ fullText, "" };
        }

        String part1 = matcher.group(1).trim();
        String part2 = matcher.group(2).trim();

        return part1.length() <= part2.length()
                ? new String[]{ part1, part2 }
                : new String[]{ part2, part1 };
    }


}
