package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistEntity extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChecklistItemEntity> items = new ArrayList<>();
}
