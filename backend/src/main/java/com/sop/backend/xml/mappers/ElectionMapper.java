package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.ElectionException;
import com.sop.backend.exceptions.XmlTransformerException;
import com.sop.backend.models.Election2;
import com.sop.backend.xml.XmlElements;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.paths.CandidatePaths;
import com.sop.backend.xml.paths.ElectionPaths;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class ElectionMapper extends Mapper<Election2> implements MapperInterface<Election2> {

    public ElectionMapper(XmlToJsonParser xmlToJsonParser) {
        super(xmlToJsonParser);
    }

    @Override
    public List<Election2> assembleModel(JsonNode jsonNode) {
        List<Election2> elections = new ArrayList<>();
        JsonNode electionIdentifierNode;

        try {
            electionIdentifierNode = getJsonNode(jsonNode, ElectionPaths.BASE_ELECTION_PATH);
        } catch (XmlTransformerException e) {
            electionIdentifierNode = getJsonNode(jsonNode, CandidatePaths.ELECTION_PATH);
        }

        if (electionIdentifierNode.isArray()) {
            throw new ElectionException("Json node is an array");
        }

        Election2 election = populateModel(electionIdentifierNode);
        elections.add(election);

        return elections;
    }

    @Override
    protected Election2 populateModel(JsonNode jsonNode) {
        JsonNode electionDateNode = jsonNode.path(XmlElements.ELECTION_DATE.getValue());
        JsonNode electionNameNode = jsonNode.path(XmlElements.ELECTION_NAME.getValue());
        JsonNode electionCategoryNode = jsonNode.path(XmlElements.ELECTION_CATEGORY.getValue());

        Election2 election = new Election2();

        LocalDate date = LocalDate.parse(getNodeValue(electionDateNode));

        election.setName(getNodeValue(electionNameNode));
        election.setCategory(getNodeValue(electionCategoryNode));
        election.setDate(date);
        election.setIdentifier(getNodeId(jsonNode));

        return election;
    }
}
