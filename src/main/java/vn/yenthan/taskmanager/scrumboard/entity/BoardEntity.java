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
@Table(name = "tbl_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardEntity extends EntityBase {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListEntity> lists = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardMemberEntity> members = new ArrayList<>();
}
