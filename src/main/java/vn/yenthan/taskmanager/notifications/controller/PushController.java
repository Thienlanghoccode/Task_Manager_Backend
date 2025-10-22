package vn.yenthan.taskmanager.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.notifications.dto.request.PushSubscriptionRequest;
import vn.yenthan.taskmanager.notifications.entity.PushSubscriptionEntity;
import vn.yenthan.taskmanager.notifications.repository.PushSubscriptionRepository;
import vn.yenthan.taskmanager.notifications.service.SimpleWebPushService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Push Controller", description = "API endpoints for web push notifications")
public class PushController {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final SimpleWebPushService webPushService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to push notifications", description = "Subscribe user to web push notifications")
    public SuccessResponse<String> subscribe(
            @RequestBody PushSubscriptionRequest request,
            Principal principal) {
        
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if subscription already exists
            var existingSubscription = pushSubscriptionRepository.findByEndpointAndUserId(
                    request.getEndpoint(), user.getId());

            if (existingSubscription.isPresent()) {
                // Update existing subscription
                PushSubscriptionEntity subscription = existingSubscription.get();
                subscription.setP256dhKey(request.getKeys().getP256dh());
                subscription.setAuthKey(request.getKeys().getAuth());
                subscription.setIsActive(true);
                pushSubscriptionRepository.save(subscription);
                
                log.info("Updated push subscription for user: {}", username);
            } else {
                // Create new subscription
                PushSubscriptionEntity subscription = PushSubscriptionEntity.builder()
                        .user(user)
                        .endpoint(request.getEndpoint())
                        .p256dhKey(request.getKeys().getP256dh())
                        .authKey(request.getKeys().getAuth())
                        .isActive(true)
                        .build();
                
                pushSubscriptionRepository.save(subscription);
                log.info("Created new push subscription for user: {}", username);
            }

            return ResponseUtil.ok(HttpStatus.OK.value(),
                    "Successfully subscribed to push notifications", "Subscribed");

        } catch (Exception e) {
            log.error("Error subscribing to push notifications: {}", e.getMessage());
            return ResponseUtil.ok(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to subscribe to push notifications", "Error");
        }
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe from push notifications", description = "Unsubscribe user from web push notifications")
    public SuccessResponse<String> unsubscribe(
            @RequestParam String endpoint,
            Principal principal) {
        
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            pushSubscriptionRepository.findByEndpointAndUserId(endpoint, user.getId())
                    .ifPresent(subscription -> {
                        subscription.setIsActive(false);
                        pushSubscriptionRepository.save(subscription);
                        log.info("Unsubscribed push notification for user: {}", username);
                    });

            return ResponseUtil.ok(HttpStatus.OK.value(), 
                    "Successfully unsubscribed from push notifications", "Unsubscribed");

        } catch (Exception e) {
            log.error("Error unsubscribing from push notifications: {}", e.getMessage());
            return ResponseUtil.ok(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to unsubscribe from push notifications", "Error");
        }
    }

    @GetMapping("/vapid-public-key")
    @Operation(summary = "Get VAPID public key", description = "Get VAPID public key for web push setup")
    public SuccessResponse<String> getVapidPublicKey() {
        String publicKey = webPushService.getVapidPublicKey();
        return ResponseUtil.ok(HttpStatus.OK.value(), 
                "VAPID public key retrieved", publicKey);
    }

    @GetMapping("/status")
    @Operation(summary = "Get Web Push status", description = "Get Web Push service status and VAPID public key")
    public SuccessResponse<Map<String, Object>> getStatus() {
        Map<String, Object> status = Map.of(
            "initialized", webPushService.isInitialized(),
            "vapidPublicKey", webPushService.getVapidPublicKey()
        );
        return ResponseUtil.ok(HttpStatus.OK.value(), "Web Push status retrieved", status);
    }

    @PostMapping("/test")
    @Operation(summary = "Test push notification", description = "Send test push notification with subscription details")
    public SuccessResponse<String> testPushNotification(@RequestBody Map<String, String> request) {
        try {
            String endpoint = request.get("endpoint");
            String p256dhKey = request.get("p256dhKey");
            String authKey = request.get("authKey");
            String title = request.getOrDefault("title", "Test Notification");
            String message = request.getOrDefault("message", "This is a test push notification");

            webPushService.sendTestNotification(endpoint, p256dhKey, authKey, title, message);
            
            return ResponseUtil.ok(HttpStatus.OK.value(), 
                    "Test push notification sent successfully", "Sent");

        } catch (Exception e) {
            log.error("Error sending test push notification: {}", e.getMessage());
            return ResponseUtil.ok(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to send test notification: " + e.getMessage(), "Error");
        }
    }
}
