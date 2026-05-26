package com.sop.backend.repositories;

import com.sop.backend.models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByFirstNameAndLastNameAndGenderAndInitialsAndQualifyingAddress(String firstName, String lastName,
                                                                                           String gender, String initials,
                                                                                           String qualifyingAddress);
}
