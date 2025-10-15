package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.CardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.ChecklistEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.members cm LEFT JOIN FETCH cm.user " +
           "WHERE c.id = :id")
    Optional<CardEntity> findByIdWithMembers(@Param("id") Long id);
    
    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.labels cl LEFT JOIN FETCH cl.label " +
           "WHERE c.id = :id")
    Optional<CardEntity> findByIdWithLabels(@Param("id") Long id);
    
    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.attachments " +
           "WHERE c.id = :id")
    Optional<CardEntity> findByIdWithAttachments(@Param("id") Long id);
    
    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.comments " +
           "WHERE c.id = :id")
    Optional<CardEntity> findByIdWithComments(@Param("id") Long id);
    
    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.checklists " +
           "WHERE c.id = :id")
    Optional<CardEntity> findByIdWithChecklists(@Param("id") Long id);
    
    @Query("SELECT ch FROM ChecklistEntity ch LEFT JOIN FETCH ch.items " +
           "WHERE ch.card.id = :cardId")
    List<ChecklistEntity> findChecklistsWithItemsByCardId(@Param("cardId") Long cardId);

    List<CardEntity> findByListIdOrderByCreatedAt(Long listId);

    @Query("SELECT c FROM CardEntity c LEFT JOIN FETCH c.members cm LEFT JOIN FETCH cm.user " +
           "WHERE c.list.id = :listId ORDER BY c.createdAt")
    List<CardEntity> findByListIdWithMembers(@Param("listId") Long listId);

    @Query("SELECT c FROM CardEntity c JOIN c.members cm WHERE cm.user.id = :userId")
    List<CardEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM CardEntity c WHERE c.list.id = :listId ORDER BY c.createdAt")
    List<CardEntity> findByListId(@Param("listId") Long listId);
}
