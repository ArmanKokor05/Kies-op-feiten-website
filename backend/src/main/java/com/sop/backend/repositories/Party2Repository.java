package com.sop.backend.repositories;

import com.sop.backend.models.Party2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Party2Repository extends JpaRepository<Party2, Long> {
    Optional<Party2> findByAcronym(String acronym);
}