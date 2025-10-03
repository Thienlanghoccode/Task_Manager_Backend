package vn.yenthan.taskmanager.core.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import vn.yenthan.taskmanager.core.auth.enums.RoleType;
import vn.yenthan.taskmanager.core.util.EntityBase;

@Getter
@Setter
@Entity
@Table(name = "tbl_role")
public class Role extends EntityBase implements GrantedAuthority {

    @Column(nullable = false,name = "name", unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    @Override
    public String getAuthority() {
        return "ROLE_" + name;
    }
}
