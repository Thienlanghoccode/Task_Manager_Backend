package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;

@Entity
@Table(name = "tbl_checklist_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemEntity extends EntityBase {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "checked")
    private Boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private ChecklistEntity checklist;
}
