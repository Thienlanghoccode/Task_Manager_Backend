package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;
import vn.yenthan.taskmanager.core.auth.entity.User;

import java.time.Instant;

@Entity
@Table(name = "tbl_board_member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardMemberEntity extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_role_id")
    private BoardRoleEntity boardRole;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "active";

    @Column(name = "joined_at")
    private Instant joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;
}
