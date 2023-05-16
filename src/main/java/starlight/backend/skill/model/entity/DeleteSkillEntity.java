package starlight.backend.skill.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "delete_skill")
public class DeleteSkillEntity {
    @Id
    private Long skillId;
    private String skill;
    private LocalDate deleteDate;
}
