package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardRoleEntity;

import java.util.Optional;

@Repository
public interface BoardRoleRepository extends JpaRepository<BoardRoleEntity, Long> {
    Optional<BoardRoleEntity> findById(Long id);
    
    /**
     * Tìm role theo boardId và name
     */
    Optional<BoardRoleEntity> findByBoardIdAndName(Long boardId, String name);
    
    /**
     * Tìm default role của board
     */
    Optional<BoardRoleEntity> findByBoardIdAndIsDefaultTrue(Long boardId);
}


