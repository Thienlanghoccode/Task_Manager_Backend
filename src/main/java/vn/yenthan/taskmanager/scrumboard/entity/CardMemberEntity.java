package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.auth.entity.User;

@Entity
@Table(name = "tbl_card_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CardMemberId.class)
public class CardMemberEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
