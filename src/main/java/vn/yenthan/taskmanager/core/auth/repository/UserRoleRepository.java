package vn.yenthan.taskmanager.core.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.core.auth.entity.Role;
import vn.yenthan.taskmanager.core.auth.entity.UserRole;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("SELECT r FROM Role r " +
           "JOIN UserRole ur ON ur.roleId = r.id " +
           "WHERE ur.userId = :userId")
    Set<Role> findRolesByUserId(Long userId);
}
