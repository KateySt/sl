package starlight.backend.skill.service;

import org.springframework.security.core.Authentication;
import starlight.backend.skill.model.request.AddSkill;
import starlight.backend.skill.model.response.ProofWithSkills;
import starlight.backend.skill.model.response.SkillList;
import starlight.backend.skill.model.response.SkillListWithPagination;

public interface SkillServiceInterface {
    SkillListWithPagination getListSkillWithFiltration(String filter, int skip, int limit);

    ProofWithSkills addSkillInYourProof(long talentId, long proofId, Authentication auth, AddSkill skills);

    SkillList getListSkillsOfProof(long proofId);

    void deleteSkill(long talentId, long proofId, long skillId, Authentication auth);
}
