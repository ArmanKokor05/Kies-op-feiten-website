package com.sop.backend.controllers;

import com.sop.backend.dto.NotificationDTO;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDTO>> save(
            @RequestBody NotificationDTO notificationDTO,
            @RequestParam Long userId) {
        NotificationDTO savedNotification = notificationService.save(notificationDTO, userId);
        return ResponseEntity.ok(new ApiResponse<>("Notification saved successfully", savedNotification));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<NotificationDTO> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>("Notifications fetched successfully", notifications));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(new ApiResponse<>("Unread count fetched successfully", unreadCount));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        NotificationDTO notification = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(new ApiResponse<>("Notification marked as read", notification));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(new ApiResponse<>("All notifications marked as read", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        notificationService.delete(id, userId);
        return ResponseEntity.ok(new ApiResponse<>("Notification deleted successfully", null));
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<Void>> deleteAll(
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        notificationService.deleteAll(userId);
        return ResponseEntity.ok(new ApiResponse<>("All notifications deleted successfully", null));
    }

    @SubscribeMapping("/notifications")
    public List<NotificationDTO> subscribeToNotifications(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return notificationService.getNotifications(userId);
    }
}