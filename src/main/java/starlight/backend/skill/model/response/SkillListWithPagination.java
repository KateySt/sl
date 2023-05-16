package starlight.backend.skill.model.response;

import lombok.Builder;
import starlight.backend.skill.model.entity.SkillEntity;

import java.util.List;

@Builder
public record SkillListWithPagination(
        long total,
        List<SkillEntity> data
) {
}
