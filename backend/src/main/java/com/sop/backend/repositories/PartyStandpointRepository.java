package com.sop.backend.repositories;

import com.sop.backend.models.PartyStandpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyStandpointRepository extends JpaRepository<PartyStandpoint, Long> {

    List<PartyStandpoint> findByPartyId(Long partyId);

    List<PartyStandpoint> findByQuestionId(Long questionId);
}