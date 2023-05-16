package starlight.backend.proof.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import starlight.backend.proof.model.enums.Status;

@Builder
public record ProofUpdateRequest(
        @Length(max = 255)
        @NotBlank
        String title,

        @Length(max = 1000)
        @NotBlank
        String description,

        @URL
        String link,

        Status status
) {
}

