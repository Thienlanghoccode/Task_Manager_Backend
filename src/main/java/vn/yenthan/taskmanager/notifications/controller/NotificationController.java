package vn.yenthan.taskmanager.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.entity.PageResponse;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.notifications.dto.response.NotificationDto;
import vn.yenthan.taskmanager.notifications.service.NotificationService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Controller", description = "API endpoints for notification management")
public class NotificationController {

    private final NotificationService notificationService;
    private final TranslateMessage translateMessage;

    @GetMapping
    @Operation(summary = "Get notifications by user ID", description = "Retrieve paginated notifications for a user")
    public PageResponse<NotificationDto> getNotificationsByUserId(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/notifications?userId={}&page={}&size={} - Fetching notifications for user", 
                userId, page, size);
        Page<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId, page, size);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieve all unread notifications for a user")
    public SuccessResponse<List<NotificationDto>> getUnreadNotifications(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("GET /api/notifications/unread?userId={} - Fetching unread notifications for user", userId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.NOTIFICATION_GET_SUCCESS),
                notificationService.getUnreadNotificationsByUserId(userId));
    }

    @GetMapping("/count")
    @Operation(summary = "Get unread notification count", description = "Get the count of unread notifications for a user")
    public SuccessResponse<Long> getUnreadNotificationCount(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("GET /api/notifications/count?userId={} - Getting unread notification count for user", userId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.NOTIFICATION_GET_SUCCESS),
                notificationService.getUnreadNotificationCount(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public SuccessResponse<String> markNotificationAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        log.info("PUT /api/notifications/{}/read - Marking notification as read", id);
        notificationService.markNotificationAsRead(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.NOTIFICATION_MARK_READ_SUCCESS));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for a user")
    public SuccessResponse<String> markAllNotificationsAsRead(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("PUT /api/notifications/read-all?userId={} - Marking all notifications as read for user", userId);
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.NOTIFICATION_MARK_ALL_READ_SUCCESS));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public SuccessResponse<String> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        log.info("DELETE /api/notifications/{} - Deleting notification", id);
        notificationService.deleteNotification(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.NOTIFICATION_DELETE_SUCCESS));
    }
}
