package starlight.backend.skill.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import starlight.backend.proof.model.entity.ProofEntity;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
public class SkillEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long skillId;

    private String skill;

    @ManyToMany(mappedBy = "skills")
    @JsonBackReference
    private Set<ProofEntity> proofs;

    public SkillEntity(String skill) {
        this.skill = skill;
    }
}