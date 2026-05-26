package com.sop.backend.repositories;

import com.sop.backend.models.PartyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyResultRepository extends JpaRepository<PartyResult, Long> {

    List<PartyResult> findByElection_ElectionYear(Integer year);

    List<PartyResult> findByElection_ElectionYearAndElection_Region(Integer year, String region);

    List<PartyResult> findByElection_ElectionYearAndElection_Municipality(Integer year, String municipality);
}