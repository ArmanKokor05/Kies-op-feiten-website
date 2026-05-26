package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.PollingStationMapperException;
import com.sop.backend.models.*;
import com.sop.backend.xml.XmlElements;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.paths.PollingStationPaths;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PollingStationMapper extends Mapper<PollingStation> implements MapperInterface<PollingStation> {

    public PollingStationMapper(XmlToJsonParser xmlToJsonParser) {
        super(xmlToJsonParser);
    }

    @Override
    public List<PollingStation> assembleModel(JsonNode jsonNode) {
        List<PollingStation> pollingStations = new ArrayList<>();
        JsonNode pollingStationNode = getJsonNode(jsonNode, PollingStationPaths.POLLING_STATION_NAME_PATH);

        if (!pollingStationNode.isArray()) {
            throw new PollingStationMapperException("Json node is not an array");
        }

        for (JsonNode node : pollingStationNode) {
            PollingStation pollingStation = populateModel(node);
            pollingStations.add(pollingStation);
        }

        return pollingStations;
    }

    @Override
    protected PollingStation populateModel(JsonNode jsonNode) {
        JsonNode identifierNode = jsonNode.path(XmlElements.REPORTING_UNIT_IDENTIFIER.getValue());

        PollingStation pollingStation = new PollingStation();

        String zipcode = extractZipcode(identifierNode);

        String name = getNodeValue(identifierNode).trim();
        name = name.replaceAll("\\(postcode:\\s*" + zipcode + "\\)", "").trim();
        name = name.replaceAll("^(Stembureau\\s+)+", "").trim();


        pollingStation.setName(name);
        pollingStation.setZipcode(zipcode);
        pollingStation.setIdentifier(getNodeId(identifierNode));

        return pollingStation;
    }

    private String extractZipcode(JsonNode jsonNode) {
        String pollingStationName = getNodeValue(jsonNode);

        Pattern zipcodePattern = Pattern.compile("\\b\\d{4}\\s?[A-Z]{2}\\b");
        Matcher zipcodeMatcher = zipcodePattern.matcher(pollingStationName);

        if  (zipcodeMatcher.find()) {
            return zipcodeMatcher.group();
        }

        return "-";
    }
}
