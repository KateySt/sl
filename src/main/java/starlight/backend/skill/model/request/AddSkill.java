package starlight.backend.skill.model.request;

import lombok.Builder;

import java.util.List;

@Builder
public record AddSkill(
        List<String> skills
) {
}
