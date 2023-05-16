package starlight.backend.advice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.advice.model.entity.DelayedDeleteEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelayDeleteRepository extends JpaRepository<DelayedDeleteEntity, Long> {
    Optional<DelayedDeleteEntity> findByEntityId(Long entityId);
    Optional<DelayedDeleteEntity> findByUserDeletingProcessUuid(UUID userDeletingProcessUUID);

    boolean existsByEntityId(Long entityID);

}
