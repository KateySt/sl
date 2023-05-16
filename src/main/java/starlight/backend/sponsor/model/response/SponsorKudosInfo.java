package starlight.backend.sponsor.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SponsorKudosInfo(
        int unusedKudos,
        int alreadyMarkedKudos,
        List<KudosWithProofId> data
) {}
