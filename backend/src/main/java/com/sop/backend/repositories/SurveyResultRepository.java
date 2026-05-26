package com.sop.backend.repositories;

import com.sop.backend.models.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    List<SurveyResult> findByUserId(Long userId);

    List<SurveyResult> findByUserIdOrderByCompletedAtDesc(Long userId);

    Optional<SurveyResult> findFirstByUserIdOrderByCompletedAtDesc(Long userId);

    void deleteByUserId(Long userId);
}