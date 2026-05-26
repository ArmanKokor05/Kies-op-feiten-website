package com.sop.backend.repositories;

import com.sop.backend.models.Election2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Election2Repository extends JpaRepository<Election2, Long> {

    public boolean existsByName(String name);

    Optional<Election2> findByName(String name);
}
