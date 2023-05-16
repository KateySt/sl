package starlight.backend.talent.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TalentUpdateRequest(
        @Length(min = 3, max = 64)
        @Pattern(regexp = "^[A-Za-z\\s'-]+$", message = "must not contain special characters")
        @JsonProperty("full_name")
        String fullName,

        @Length(min = 10, max = 10)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)\\S+$", message = "The date must be in the format: yyyy-MM-dd")
        LocalDate birthday,

        @Length(min = 8, max = 128)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)\\S+$",
                message = "must be between 8 and 128 characters, must contain at least one letter and one number")
        String password,

        @URL
        String avatar,

        String education,

        String experience,

        List<String> positions
) {
}
