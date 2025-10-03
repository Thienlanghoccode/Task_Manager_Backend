package vn.yenthan.taskmanager.core.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.core.auth.entity.Role;
import vn.yenthan.taskmanager.core.auth.enums.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleType name);
}
