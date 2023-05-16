package starlight.backend.proof.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import starlight.backend.proof.model.enums.Status;

import java.time.Instant;

@Builder
public record ProofFullInfo(
        long id,
        String title,
        String description,
        String link,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant dateCreated,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant dateLastUpdated,
        Status status
) {
}
