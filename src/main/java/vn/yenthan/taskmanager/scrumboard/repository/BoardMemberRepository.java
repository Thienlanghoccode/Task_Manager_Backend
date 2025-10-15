package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardMemberEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMemberEntity, Long> {

    @Query("SELECT bm FROM BoardMemberEntity bm LEFT JOIN FETCH bm.user u " +
           "LEFT JOIN FETCH bm.boardRole br WHERE bm.board.id = :boardId")
    List<BoardMemberEntity> findByBoardIdWithDetails(@Param("boardId") Long boardId);

    @Query("SELECT bm FROM BoardMemberEntity bm LEFT JOIN FETCH bm.user u " +
           "LEFT JOIN FETCH bm.boardRole br WHERE bm.board.id = :boardId AND bm.status = 'active'")
    List<BoardMemberEntity> findActiveByBoardId(@Param("boardId") Long boardId);

    Optional<BoardMemberEntity> findByBoardIdAndUserId(Long boardId, Long userId);

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    @Query("SELECT COUNT(bm) FROM BoardMemberEntity bm WHERE bm.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // Note: This query would need to be in CardMemberRepository
    // Long countCardMembershipsByUserId(@Param("userId") Long userId);
}
