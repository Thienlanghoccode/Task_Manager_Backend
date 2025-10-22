package vn.yenthan.taskmanager.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.notifications.entity.PushSubscriptionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, Long> {

    List<PushSubscriptionEntity> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT ps FROM PushSubscriptionEntity ps WHERE ps.user.id = :userId AND ps.isActive = true")
    List<PushSubscriptionEntity> findActiveByUserId(@Param("userId") Long userId);

    Optional<PushSubscriptionEntity> findByEndpoint(String endpoint);

    @Query("SELECT ps FROM PushSubscriptionEntity ps WHERE ps.endpoint = :endpoint AND ps.user.id = :userId")
    Optional<PushSubscriptionEntity> findByEndpointAndUserId(@Param("endpoint") String endpoint, @Param("userId") Long userId);

    void deleteByUserId(Long userId);

    void deleteByEndpoint(String endpoint);
}
