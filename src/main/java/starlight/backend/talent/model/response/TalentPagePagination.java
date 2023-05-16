package starlight.backend.talent.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TalentPagePagination(
       long total,
       List<TalentProfile> data
) {}
