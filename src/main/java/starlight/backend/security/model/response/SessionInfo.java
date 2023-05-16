package starlight.backend.security.model.response;

import lombok.Builder;

@Builder
public record SessionInfo(
        String token
) {
}
