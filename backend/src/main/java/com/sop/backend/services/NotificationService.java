package com.sop.backend.services;

import com.sop.backend.dto.NotificationDTO;
import com.sop.backend.models.Notification;
import com.sop.backend.models.User;
import com.sop.backend.exceptions.NotificationException;
import com.sop.backend.repositories.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    public NotificationDTO save(NotificationDTO notificationDTO, Long userId) {
        System.out.println("save notification method called");

        String title = notificationDTO.title().trim();
        String message = notificationDTO.message().trim();

        if (title.isEmpty()) {
            throw new NotificationException("Title is empty");
        }

        if (message.isEmpty()) {
            throw new NotificationException("Message is empty");
        }

        User user = userService.findById(userId);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setData(notificationDTO.data());
        notification.setActionUrl(notificationDTO.actionUrl());
        notification.setUser(user);

        notification = this.notificationRepository.save(notification);

        NotificationDTO savedNotification = toDTO(notification);

        System.out.println("should send websocket notification to user");
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                savedNotification
        );
        System.out.println("Sent WebSocket notification to user: " + userId);

        return savedNotification;
    }

    public List<NotificationDTO> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationDTO markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationException("Unauthorized access to notification");
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);

        return toDTO(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationException("Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAll(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    private NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRead(),
                notification.getData(),
                notification.getActionUrl()
        );
    }

    public void createNotificationWithData(User toUser, String title, String message, String actionUrl, String data) {
        System.out.println("Creating notification for user: " + toUser.getName());

        NotificationDTO notificationDTO = new NotificationDTO(
                null,
                null,
                title,
                message,
                false,
                data,
                actionUrl
        );

        save(notificationDTO, toUser.getId());
    }
}