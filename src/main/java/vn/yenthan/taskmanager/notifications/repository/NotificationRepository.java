package vn.yenthan.taskmanager.notifications.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.notifications.entity.NotificationEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n LEFT JOIN FETCH n.actor a " +
           "WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<NotificationEntity> findByUserIdWithActor(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM NotificationEntity n LEFT JOIN FETCH n.actor a " +
           "WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<NotificationEntity> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM NotificationEntity n LEFT JOIN FETCH n.actor a " +
           "WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}
