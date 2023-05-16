package starlight.backend.user.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import starlight.backend.proof.model.entity.ProofEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
public class UserEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private LocalDate birthday;
    @URL
    private String avatar;
    @Length(max = 255)
    private String education;
    @Length(max = 255)
    private String experience;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "user_position",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id"))
    @JsonManagedReference
    private Set<PositionEntity> positions;

    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<String> authorities;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private Set<ProofEntity> proofs;
}
