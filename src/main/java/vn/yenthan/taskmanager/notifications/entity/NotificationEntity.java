package vn.yenthan.taskmanager.notifications.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.CardEntity;

@Entity
@Table(name = "tbl_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends EntityBase {

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;
}
