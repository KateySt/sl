package starlight.backend.sponsor.model.response;

import lombok.Builder;

@Builder
public record SponsorFullInfo(
        String fullName,
        String avatar,
        String company,
        int unusedKudos
) {
}
