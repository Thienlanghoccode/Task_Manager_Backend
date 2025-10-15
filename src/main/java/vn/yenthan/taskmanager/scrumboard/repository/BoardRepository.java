package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b FROM BoardEntity b LEFT JOIN FETCH b.lists " +
           "WHERE b.id = :id")
    Optional<BoardEntity> findByIdWithLists(@Param("id") Long id);
    
    @Query("SELECT b FROM BoardEntity b LEFT JOIN FETCH b.members " +
           "WHERE b.id = :id")
    Optional<BoardEntity> findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT b FROM BoardEntity b LEFT JOIN FETCH b.lists " +
           "ORDER BY b.createdAt DESC")
    List<BoardEntity> findAllWithLists();

    @Query("SELECT b FROM BoardEntity b JOIN b.members bm WHERE bm.user.id = :userId")
    List<BoardEntity> findByUserId(@Param("userId") Long userId);

    boolean existsByName(String name);
}
