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
@Table(name = "tbl_board_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRoleEntity extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_default")
    private Boolean isDefault;

    @OneToMany(mappedBy = "boardRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardMemberEntity> members = new ArrayList<>();
}
