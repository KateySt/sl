package starlight.backend.proof.model.response;

import lombok.Builder;

@Builder
public record SponsorOnProofShortInfo(
        String sponsorName,
        int countKudos,
        String sponsorAvatarUrl
) {
}
