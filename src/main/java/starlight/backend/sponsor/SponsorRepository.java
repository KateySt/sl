package starlight.backend.sponsor;

import org.springframework.data.jpa.repository.JpaRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;

import java.util.Optional;

public interface SponsorRepository extends JpaRepository<SponsorEntity, Long> {
    Optional<SponsorEntity> findBySponsorId(Long sponsorId);
    boolean existsByEmail(String email);

    Optional<SponsorEntity> findByEmail(String email);

    Optional<SponsorEntity> findByActivationCode(String token);

    boolean existsByActivationCode(String activationCode);

    boolean existsBySponsorId(long sponsorId);
}