package starlight.backend.email.model;

import jakarta.validation.constraints.NotBlank;

public record ChangePassword(
        @NotBlank
        String password
) {
}
