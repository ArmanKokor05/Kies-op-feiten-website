package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.Party2Repository;
import com.sop.backend.xml.mappers.PartyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyService {
    private final PartyMapper partyMapper;
    private final Party2Repository party2Repository;

    public PartyService(PartyMapper partyMapper, Party2Repository party2Repository) {
        this.partyMapper = partyMapper;
        this.party2Repository = party2Repository;
    }

    public List<Party2> getParties() { return party2Repository.findAll(); }

    public Party2 save(Party2 party) {
        return party2Repository.findByAcronym(party.getAcronym())
                .orElseGet(() -> party2Repository.save(party));
    }

    public List<JsonNode> getAffiliationNodes(JsonNode jsonNode) {
        return partyMapper.getAffiliationNodes(jsonNode);
    }

    public Party2 getCandidateParty(JsonNode affiliation) {
        return partyMapper.AssembleCandidatePartyModel(affiliation);
    }
}
