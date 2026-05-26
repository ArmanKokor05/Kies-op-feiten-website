package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.dto.CreateCandidatesDTO;
import com.sop.backend.models.*;
import com.sop.backend.repositories.CandidateRepository;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.mappers.CandidateMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final XmlToJsonParser xmlToJsonParser;
    private final CandidateMapper candidateMapper;
    private final ElectionService electionService;
    private final PartyService partyService;
    private final CandidateListEntryService candidateListEntryService;

    public CandidateService(CandidateRepository candidateRepository, XmlToJsonParser xmlToJsonParser, CandidateMapper candidateMapper, ElectionService electionService, PartyService partyService, CandidateListEntryService candidateListEntryService) {
        this.candidateRepository = candidateRepository;
        this.xmlToJsonParser = xmlToJsonParser;
        this.candidateMapper = candidateMapper;
        this.electionService = electionService;
        this.partyService = partyService;
        this.candidateListEntryService = candidateListEntryService;
    }

    public CreateCandidatesDTO createCandidate(MultipartFile[] file) throws IOException {
        List<JsonNode> jsonNodes = xmlToJsonParser.parseMultipleFilesToJson(file);
        int amountAdded = 0;

        for (JsonNode jsonNode : jsonNodes) {

            Election2 election = electionService.parseFromJson(jsonNode);
            election = electionService.save(election);

            List<JsonNode> affiliations = partyService.getAffiliationNodes(jsonNode);

            amountAdded = createCandidates(affiliations, election);
        }

        CreateCandidatesDTO dto = new CreateCandidatesDTO();
        dto.setAmount(amountAdded);

        return dto;
    }

    private int createCandidates(List<JsonNode> affiliations, Election2 election) {
        int amountAdded = 0;

        for (JsonNode affiliation : affiliations) {

            Party2 party = partyService.getCandidateParty(affiliation);
            party = partyService.save(party);

            List<Candidate> candidates = candidateMapper.assembleModel(affiliation);

            amountAdded += candidates.size();

            saveAll(candidates, election, party);
        }

       return amountAdded;
    }

    public void saveAll(List<Candidate> candidates, Election2 election, Party2 party) {

        for (Candidate candidate : candidates) {
            Candidate savedCandidate = save(candidate);

            saveRelationalModels(savedCandidate, election, party);
        }
    }

    public Candidate save(Candidate candidate) {
        return candidateRepository.findByFirstNameAndLastNameAndGenderAndInitialsAndQualifyingAddress(
                        candidate.getFirstName(),
                        candidate.getLastName(),
                        candidate.getGender(),
                        candidate.getInitials(),
                        candidate.getQualifyingAddress()
                        )
                .orElseGet(() -> candidateRepository.save(candidate));
    }

    private void saveRelationalModels(Candidate candidate, Election2 election, Party2 party) {
        CandidateListEntry candidateListEntry = candidateListEntryService.createCandidateListEntry(candidate, election, party);
        candidateListEntryService.save(candidateListEntry);
    }
}