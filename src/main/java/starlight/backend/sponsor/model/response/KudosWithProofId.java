package starlight.backend.sponsor.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record KudosWithProofId(
        long kudosId,
        long talentId,
        int countKudos,
        Instant updateData,
        Instant createData,
        long proofId
) {}
