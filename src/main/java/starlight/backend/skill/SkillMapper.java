package starlight.backend.skill;

import org.mapstruct.Mapper;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.skill.model.entity.SkillEntity;
import starlight.backend.skill.model.response.ProofWithSkills;
import starlight.backend.skill.model.response.SkillList;
import starlight.backend.skill.model.response.SkillListWithPagination;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface SkillMapper {
    default SkillListWithPagination toSkillListWithPagination(List<SkillEntity> skills, long pageNumber) {
        return SkillListWithPagination.builder()
                .data(skills)
                .total(pageNumber)
                .build();
    }

    default SkillList toSkillList(List<SkillEntity> skills) {
        return SkillList.builder()
                .skills(skills)
                .build();
    }

    default ProofWithSkills toProofWithSkills(ProofEntity proofEntity) {
        return ProofWithSkills.builder()
                .title(proofEntity.getTitle())
                .link(proofEntity.getLink())
                .status(proofEntity.getStatus())
                .description(proofEntity.getDescription())
                .dateLastUpdated(proofEntity.getDateLastUpdated())
                .dateCreated(proofEntity.getDateCreated())
                .skill(proofEntity.getSkills().stream().toList())
                .build();
    }

}
