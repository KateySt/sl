package starlight.backend.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.skill.model.entity.DeleteSkillEntity;

@Repository
public interface DeleteSkillRepository extends JpaRepository<DeleteSkillEntity, Long> {
}