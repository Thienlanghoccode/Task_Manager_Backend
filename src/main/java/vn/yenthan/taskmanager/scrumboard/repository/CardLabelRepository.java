package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.CardLabelEntity;

import java.util.List;

@Repository
public interface CardLabelRepository extends JpaRepository<CardLabelEntity, Long> {

    @Query("SELECT cl FROM CardLabelEntity cl LEFT JOIN FETCH cl.label WHERE cl.card.id = :cardId")
    List<CardLabelEntity> findByCardIdWithLabel(@Param("cardId") Long cardId);

    List<CardLabelEntity> findByCardId(Long cardId);

    List<CardLabelEntity> findByLabelId(Long labelId);

    boolean existsByCardIdAndLabelId(Long cardId, Long labelId);

    @Modifying
    @Query("DELETE FROM CardLabelEntity cl WHERE cl.card.id = :cardId")
    void deleteByCardId(@Param("cardId") Long cardId);

    @Modifying
    @Query("DELETE FROM CardLabelEntity cl WHERE cl.card.id = :cardId AND cl.label.id = :labelId")
    void deleteByCardIdAndLabelId(@Param("cardId") Long cardId, @Param("labelId") Long labelId);
}
