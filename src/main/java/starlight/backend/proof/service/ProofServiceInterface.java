package starlight.backend.proof.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.request.ProofAddRequest;
import starlight.backend.proof.model.request.ProofUpdateRequest;
import starlight.backend.proof.model.response.ProofFullInfo;
import starlight.backend.proof.model.response.ProofPagePagination;

public interface ProofServiceInterface {
    ProofPagePagination proofsPagination(int page, int size, boolean sort);

    ProofEntity addProofProfile(long talentId, ProofAddRequest proofUpdateRequest);

    ResponseEntity<?> getLocation(long talentId, ProofAddRequest proofAddRequest, Authentication auth);

    void deleteProof(long talentId, long proofId, Authentication auth);

    ProofPagePagination getTalentAllProofs(Authentication auth, long talentId, int page, int size, boolean sort, String status);

    ProofFullInfo getProofFullInfo(Authentication auth, long proofId);

    ProofFullInfo proofUpdateRequest(long talentId, long id, ProofUpdateRequest proofUpdateRequest, Authentication auth);
    ProofPagePagination getTalentAllProofsWithKudoses(Authentication auth, long talentId,
                                                      int page, int size, boolean sort, String status);
}
