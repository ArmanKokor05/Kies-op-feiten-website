package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.models.CandidateResult;
import com.sop.backend.models.Election;
import com.sop.backend.models.PartyResult;
import com.sop.backend.repositories.ElectionRepository;
import com.sop.backend.repositories.CandidateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
public class XmlService {

    private final XmlToJsonParser xmlToJsonParser;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    public XmlService(ElectionRepository electionRepository, CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.xmlToJsonParser = new XmlToJsonParser();
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found with id: " + id));
    }

    public Election processXmlFile(MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xml")) {
            throw new IllegalArgumentException("Invalid file");
        }

        JsonNode data = parseXml(file);
        validateEmlData(data);
        return saveToDatabase(data);
    }

    public List<Election> processBatchXmlFiles(MultipartFile[] files) {
        List<Election> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                results.add(processXmlFile(file));
            } catch (Exception e) {
                // Ignore failed files
            }
        }
        return results;
    }

    private JsonNode parseXml(MultipartFile file) {
        try {
            return xmlToJsonParser.parseXmlToJson(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML");
        }
    }

    private void validateEmlData(JsonNode root) {
        JsonNode count = root.path("Count");
        JsonNode election = count.path("Election");
        JsonNode electionId = election.path("ElectionIdentifier");

        if (count.isMissingNode() || election.isMissingNode()) {
            throw new IllegalArgumentException("Invalid EML structure");
        }

        if (!electionId.has("ElectionName") || !electionId.has("ElectionDate")) {
            throw new IllegalArgumentException("Missing required fields");
        }
    }

    @Transactional
    protected Election saveToDatabase(JsonNode root) {
        JsonNode count = root.path("Count");
        JsonNode electionNode = count.path("Election");
        JsonNode electionIdNode = electionNode.path("ElectionIdentifier");

        String electionId = electionIdNode.path("Id").asText("");
        String electionName = electionIdNode.path("ElectionName").asText("Onbekend");
        String electionCategory = electionIdNode.path("ElectionCategory").asText("");

        // Municipality uit ManagingAuthority (AuthorityIdentifier)
        JsonNode authorityNode = root.path("ManagingAuthority").path("AuthorityIdentifier");
        String municipalityId = authorityNode.get("Id").asText("");
        String municipality = authorityNode.get("").asText(""); // Haalt de waarde uit het lege veld

        System.out.println("Municipality: '" + municipality + "' (ID: '" + municipalityId + "')");

        String electionDateStr = "";

        Iterator<String> fieldNames = electionIdNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.contains("ElectionDate") || fieldName.contains("Date")) {
                String value = electionIdNode.path(fieldName).asText("");
                if (!value.isEmpty() && value.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                    electionDateStr = value;
                    System.out.println("Found date in field: " + fieldName + " = " + value);
                    break;
                }
            }
        }

        if (electionDateStr.isEmpty()) {
            if (electionIdNode.has("kr:ElectionDate")) {
                electionDateStr = electionIdNode.get("kr:ElectionDate").asText("");
            } else if (electionIdNode.has("ElectionDate")) {
                electionDateStr = electionIdNode.get("ElectionDate").asText("");
            } else if (electionIdNode.has("kr:CreationDateTime")) {
                electionDateStr = electionIdNode.get("kr:CreationDateTime").asText("");
            }
        }

        System.out.println("Final date string: '" + electionDateStr + "'");

        LocalDate electionDate = LocalDate.now();
        if (electionDateStr != null && !electionDateStr.isEmpty() && electionDateStr.length() >= 10) {
            try {
                String datePart = electionDateStr.substring(0, 10);
                electionDate = LocalDate.parse(datePart);
                System.out.println("Successfully parsed date: " + electionDate);
            } catch (Exception e) {
                System.err.println("Failed to parse date: '" + electionDateStr + "'");
                e.printStackTrace();
            }
        } else {
            System.err.println("WARNING: No valid date found, using current date");
        }

        JsonNode contest = electionNode.path("Contests").path("Contest");
        String regionId = contest.path("ContestIdentifier").path("Id").asText("");
        String region = contest.path("ContestIdentifier").path("ContestName").asText("");
        Integer totalCounted = contest.path("TotalVotes").path("TotalCounted").asInt(0);
        Integer cast = contest.path("TotalVotes").path("Cast").asInt(0);

        System.out.println("=== Saving Election Data ===");
        System.out.println("Election: " + electionName);
        System.out.println("Date: " + electionDate);
        System.out.println("Municipality: " + municipality);
        System.out.println("MunicipalityId: " + municipalityId);
        System.out.println("Region: " + region);
        System.out.println("RegionId: " + regionId);

        Election election = electionRepository.findByElectionIdAndRegionAndMunicipalityId(electionId, region, municipalityId)
                .orElse(new Election());

        election.setElectionId(electionId);
        election.setElectionName(electionName);
        election.setElectionCategory(electionCategory);
        election.setElectionDate(electionDate);
        election.setMunicipality(municipality);
        election.setMunicipalityId(municipalityId);
        election.setRegion(region);
        election.setRegionId(regionId);
        election.setTotalCounted(totalCounted);
        election.setCast(cast);

        election.getPartyResults().clear();

        Map<String, PartyResult> partyMap = new HashMap<>();

        JsonNode selections = contest.path("TotalVotes").path("Selection");

        if (selections.isArray()) {
            for (JsonNode selection : selections) {
                if (selection.has("AffiliationIdentifier") && !selection.has("Candidate")) {
                    String partyId = selection.path("AffiliationIdentifier").path("Id").asText("");
                    String partyName = selection.path("AffiliationIdentifier").path("RegisteredName").asText("");
                    Integer votes = selection.path("ValidVotes").asInt(0);

                    System.out.println("Party: " + partyName + " (" + partyId + ") - Votes: " + votes);

                    PartyResult party = new PartyResult();
                    party.setPartyId(partyId);
                    party.setPartyName(partyName);
                    party.setValidVotes(votes);

                    election.addPartyResult(party);
                    partyMap.put(partyId, party);
                }
            }

            for (JsonNode selection : selections) {
                if (selection.has("Candidate")) {
                    String candidateId = selection.path("Candidate").path("CandidateIdentifier").path("Id").asText("");
                    Integer candidateVotes = selection.path("ValidVotes").asInt(0);

                    String partyId = "";
                    if (selection.has("AffiliationIdentifier")) {
                        partyId = selection.path("AffiliationIdentifier").path("Id").asText("");
                    }

                    System.out.println("Candidate: " + candidateId + " - Party: " + partyId + " - Votes: " + candidateVotes);

                    PartyResult party = partyMap.get(partyId);
                    if (party != null) {
                        CandidateResult candidate = new CandidateResult();
                        candidate.setCandidateId(candidateId);
                        candidate.setValidVotes(candidateVotes);
                        party.addCandidateResult(candidate);
                    } else {
                        System.err.println("WARNING: Candidate " + candidateId + " has unknown party: " + partyId);
                    }
                }
            }
        }

        Election saved = electionRepository.save(election);
        System.out.println("Successfully saved election with " + partyMap.size() + " parties");
        return saved;
    }
}