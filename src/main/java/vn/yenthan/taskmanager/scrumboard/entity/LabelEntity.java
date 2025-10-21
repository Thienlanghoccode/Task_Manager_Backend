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
@Table(name = "tbl_label")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelEntity extends EntityBase {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color", nullable = false)
    private String color;

    @OneToMany(mappedBy = "label", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardLabelEntity> cardLabels = new ArrayList<>();
}
