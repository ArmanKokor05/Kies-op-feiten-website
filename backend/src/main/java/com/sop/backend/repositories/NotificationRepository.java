package com.sop.backend.repositories;

import com.sop.backend.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Long countByReadFalse();
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    Long countByUserIdAndReadFalse(Long userId);
    void deleteAllByUserId(Long userId);
}