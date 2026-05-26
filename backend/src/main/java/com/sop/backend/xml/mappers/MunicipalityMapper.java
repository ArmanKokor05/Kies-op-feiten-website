package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.MunicipalityException;
import com.sop.backend.models.Municipality;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.paths.MunicipalityPaths;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MunicipalityMapper extends Mapper<Municipality> implements MapperInterface<Municipality> {

    public MunicipalityMapper(XmlToJsonParser xmlToJsonParser) {
        super(xmlToJsonParser);
    }

    @Override
    public List<Municipality> assembleModel(JsonNode jsonNode) {
        List<Municipality> municipality = new ArrayList<>();
        JsonNode municipalityNode = getJsonNode(jsonNode, MunicipalityPaths.MUNICIPALITY_NAME_PATH);

        if (municipalityNode.isArray()) {
            throw new MunicipalityException("Json node is an array");
        }

        Municipality municipalityObject = populateModel(municipalityNode);
        municipality.add(municipalityObject);

        return municipality;
    }

    @Override
    protected Municipality populateModel(JsonNode jsonNode) {
        Municipality municipality = new Municipality();

        String municipalityName = getNodeValue(jsonNode);
        if ("De Kiesraad".equals(municipalityName)) throw new MunicipalityException("Municipality name is De Kiesraad, wrong XML file");

        municipality.setName(municipalityName);
        return municipality;
    }
}
