package starlight.backend.sponsor.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Builder
public record SponsorUpdateRequest(
        @Length(min = 3, max = 64)
        @Pattern(regexp = "^[A-Za-z\\s'-]+$", message = "must not contain special characters")
        @JsonProperty("full_name")
        String fullName,
        @URL
        String avatar,
        String company,
        @Positive
        @Min(0)
        @JsonProperty("unused_kudos")
        int unusedKudos
) {
}
