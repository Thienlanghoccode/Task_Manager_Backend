package vn.yenthan.taskmanager.notifications.service;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.Security;

@Service
public class SimpleWebPushService {

    @Value("${webpush.vapid.public-key:}")
    private String vapidPublicKey;
    
    @Value("${webpush.vapid.private-key:}")
    private String vapidPrivateKey;
    
    @Value("${webpush.vapid.subject:mailto:admin@taskmanager.com}")
    private String vapidSubject;

    private PushService pushService;

    @PostConstruct
    public void init() throws GeneralSecurityException {
        // Add BouncyCastle provider for encryption
        Security.addProvider(new BouncyCastleProvider());
        
        if (!vapidPublicKey.isEmpty() && !vapidPrivateKey.isEmpty()) {
            this.pushService = new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
            System.out.println("✅ WebPushService initialized successfully with VAPID keys");
        } else {
            System.out.println("⚠️ VAPID keys not configured. Web Push notifications will not work.");
        }
    }

    public void sendTestNotification(String endpoint, String p256dhKey, String authKey, String title, String message) {
        if (pushService == null) {
            System.out.println("❌ PushService not initialized. Cannot send push notification.");
            return;
        }

        try {
            // Create subscription object
            Subscription.Keys keys = new Subscription.Keys(
                    p256dhKey,
                    authKey
            );
            Subscription webPushSubscription = new Subscription(endpoint, keys);

            // Create notification payload
            String payload = createNotificationPayload(title, message, "/dashboard");
            Notification notification = new Notification(webPushSubscription, payload);

            // Send push notification
            pushService.send(notification);
            System.out.println("✅ Push notification sent successfully to endpoint: " + endpoint);

        } catch (Exception e) {
            System.out.println("❌ Failed to send push notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createNotificationPayload(String title, String message, String url) {
        return String.format("""
            {
                "title": "%s",
                "body": "%s",
                "icon": "/icon-192x192.png",
                "badge": "/badge-72x72.png",
                "url": "%s",
                "timestamp": %d
            }
            """, title, message, url != null ? url : "", System.currentTimeMillis());
    }

    public String getVapidPublicKey() {
        return vapidPublicKey;
    }
    
    public boolean isInitialized() {
        return pushService != null;
    }
}
