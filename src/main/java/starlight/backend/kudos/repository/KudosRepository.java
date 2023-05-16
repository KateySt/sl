package starlight.backend.kudos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.kudos.model.entity.KudosEntity;

import java.util.List;

@Repository
public interface KudosRepository extends JpaRepository<KudosEntity, Long> {
    KudosEntity findByOwner_SponsorIdAndProof_ProofId(Long sponsorId, Long proofId);
    List<KudosEntity> findByProof_ProofId(long proofId);
}
