package starlight.backend.proof.model.request;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Builder
public record ProofAddRequest(
        @Length(max = 255)
        String title,

        @Length(max = 1000)
        String description,

        @URL
        String link

) {
}
