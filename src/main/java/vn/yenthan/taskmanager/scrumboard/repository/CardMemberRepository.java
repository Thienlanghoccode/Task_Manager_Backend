package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.CardMemberEntity;

import java.util.List;

@Repository
public interface CardMemberRepository extends JpaRepository<CardMemberEntity, Long> {

    @Query("SELECT cm FROM CardMemberEntity cm LEFT JOIN FETCH cm.user u WHERE cm.card.id = :cardId")
    List<CardMemberEntity> findByCardIdWithUser(@Param("cardId") Long cardId);

    List<CardMemberEntity> findByCardId(Long cardId);

    List<CardMemberEntity> findByUserId(Long userId);

    boolean existsByCardIdAndUserId(Long cardId, Long userId);

    @Modifying
    @Query("DELETE FROM CardMemberEntity cm WHERE cm.card.id = :cardId")
    void deleteByCardId(@Param("cardId") Long cardId);

    @Modifying
    @Query("DELETE FROM CardMemberEntity cm WHERE cm.card.id = :cardId AND cm.user.id = :userId")
    void deleteByCardIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Query("SELECT COUNT(cm) FROM CardMemberEntity cm WHERE cm.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
