package starlight.backend.email.model;

import jakarta.validation.constraints.NotBlank;

public record Email(
        @NotBlank
        String subject,
        @NotBlank
        String text,
        String pathToAttachment
) {
}