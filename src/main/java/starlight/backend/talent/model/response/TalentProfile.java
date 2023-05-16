package starlight.backend.talent.model.response;

import lombok.Builder;

@Builder
public record TalentProfile(
        long id,
        String fullName,
        String position,
        String avatar
) {
}
