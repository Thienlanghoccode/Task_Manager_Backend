package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_card_label")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CardLabelId.class)
public class CardLabelEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", nullable = false)
    private LabelEntity label;
}
