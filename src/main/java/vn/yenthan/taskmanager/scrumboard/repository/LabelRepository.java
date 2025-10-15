package vn.yenthan.taskmanager.scrumboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yenthan.taskmanager.scrumboard.entity.LabelEntity;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<LabelEntity, Long> {

    List<LabelEntity> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
