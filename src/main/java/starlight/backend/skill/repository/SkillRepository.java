package starlight.backend.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.skill.model.entity.SkillEntity;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    SkillEntity findBySkillIdAndProofs_ProofIdAndProofs_User_UserId(Long skillId, Long proofId, Long userId);

    boolean existsBySkillIdAndProofs_ProofIdAndProofs_User_UserId(Long skillId, Long proofId, Long userId);

    SkillEntity findBySkillIgnoreCase(String skill);

    boolean existsBySkillIgnoreCase(String skill);
}