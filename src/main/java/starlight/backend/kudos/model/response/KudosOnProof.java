package starlight.backend.kudos.model.response;

import lombok.Builder;

@Builder
public record  KudosOnProof(
      int kudosOnProof,
      int kudosFromMe,
      boolean isKudosed
) {}
