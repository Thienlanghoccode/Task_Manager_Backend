package vn.yenthan.taskmanager.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.notifications.dto.response.NotificationDto;
import vn.yenthan.taskmanager.notifications.entity.NotificationEntity;
import vn.yenthan.taskmanager.notifications.mapper.NotificationMapper;
import vn.yenthan.taskmanager.notifications.repository.NotificationRepository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.CardEntity;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRepository;
import vn.yenthan.taskmanager.scrumboard.repository.CardRepository;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByUserId(Long userId, int page, int size) {
        log.info("Fetching notifications for user {} with pagination: page={}, size={}", userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> notifications = notificationRepository.findByUserIdWithActor(userId, pageable);
        return notifications.map(notificationMapper::toNotificationDto);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotificationsByUserId(Long userId) {
        log.info("Fetching unread notifications for user {}", userId);
        List<NotificationEntity> notifications = notificationRepository.findUnreadByUserId(userId);
        return notificationMapper.toNotificationDtoList(notifications);
    }

    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId) {
        log.info("Getting unread notification count for user {}", userId);
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markNotificationAsRead(Long notificationId) {
        log.info("Marking notification {} as read", notificationId);
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("Notification marked as read successfully");
    }

    public void markAllNotificationsAsRead(Long userId) {
        log.info("Marking all notifications as read for user {}", userId);
        List<NotificationEntity> unreadNotifications = notificationRepository.findUnreadByUserId(userId);
        
        for (NotificationEntity notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        
        notificationRepository.saveAll(unreadNotifications);
        log.info("All notifications marked as read successfully");
    }

    public void createNotification(String type, String title, String message, Long userId, 
                                 Long boardId, Long cardId, Long actorId, String metadata) {
        log.info("Creating notification: type={}, title={}, userId={}", type, title, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new NotFoundException("Actor not found with id: " + actorId));
        
        NotificationEntity notification = new NotificationEntity();
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUser(user);
        notification.setActor(actor);
        notification.setIsRead(false);
        notification.setMetadata(metadata);
        
        if (boardId != null) {
            BoardEntity board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
            notification.setBoard(board);
        }
        
        if (cardId != null) {
            CardEntity card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));
            notification.setCard(card);
        }
        
        notificationRepository.save(notification);
        log.info("Notification created successfully with id: {}", notification.getId());
    }

    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification with id: {}", notificationId);
        
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        
        notificationRepository.deleteById(notificationId);
        log.info("Notification deleted successfully");
    }
}
