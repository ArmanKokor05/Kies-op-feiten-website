package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.dto.CreateElectionsDTO;
import com.sop.backend.models.Election2;
import com.sop.backend.repositories.Election2Repository;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.mappers.ElectionMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElectionService {

    private final XmlToJsonParser xmlToJsonParser;
    private final ElectionMapper electionMapper;
    private final Election2Repository election2Repository;

    public ElectionService(XmlToJsonParser xmlToJsonParser, ElectionMapper electionMapper, Election2Repository election2Repository) {
        this.xmlToJsonParser = xmlToJsonParser;
        this.electionMapper = electionMapper;
        this.election2Repository = election2Repository;
    }

    public CreateElectionsDTO createElection(MultipartFile[] file) throws IOException {
        List<JsonNode> jsonNodes = xmlToJsonParser.parseMultipleFilesToJson(file);
        List<Election2> elections = new ArrayList<>();

        for (JsonNode jsonNode : jsonNodes) {
            elections.addAll(electionMapper.assembleModel(jsonNode));
        }

        for (Election2 election : elections) {
            save(election);
        }

        CreateElectionsDTO dto = new CreateElectionsDTO();
        dto.setAmount(elections.size());

        return dto;
    }

    public List<Election2> getAllElections() {
        return election2Repository.findAll();
    }

    public Election2 parseFromJson (JsonNode jsonNode) {
        List<Election2> elections = electionMapper.assembleModel(jsonNode);

        if (elections.size() != 1) throw new RuntimeException("Expected exactly one election");

        return elections.get(0);
    }

    public Election2 save(Election2 election) {
        return election2Repository.findByName(election.getName())
                .orElseGet(() -> election2Repository.save(election));
    }
}
