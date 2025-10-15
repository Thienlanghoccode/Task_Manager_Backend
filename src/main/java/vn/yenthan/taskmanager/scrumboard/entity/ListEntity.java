package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListEntity extends EntityBase {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CardEntity> cards = new HashSet<>();
}
