package starlight.backend.user.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long positionId;

    private String position;

    @ManyToMany(mappedBy = "positions")
    @JsonBackReference
    private Set<UserEntity> users;

    public PositionEntity(String position) {
        this.position = position;
    }
}
