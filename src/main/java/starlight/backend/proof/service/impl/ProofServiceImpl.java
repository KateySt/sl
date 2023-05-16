package starlight.backend.proof.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.proof.UserAccesDeniedToProofException;
import starlight.backend.exception.proof.UserCanNotEditProofNotInDraftException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.repository.KudosRepository;
import starlight.backend.proof.ProofMapper;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.enums.Status;
import starlight.backend.proof.model.request.ProofAddRequest;
import starlight.backend.proof.model.request.ProofUpdateRequest;
import starlight.backend.proof.model.response.ProofFullInfo;
import starlight.backend.proof.model.response.ProofPagePagination;
import starlight.backend.proof.service.ProofServiceInterface;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.user.repository.UserRepository;

import java.net.URI;
import java.time.Instant;

@AllArgsConstructor
@Service
@Transactional
public class ProofServiceImpl implements ProofServiceInterface {
    private final String DATA_CREATED = "dateCreated";
    private ProofRepository repository;
    private UserRepository userRepository;
    private ProofMapper mapper;
    private KudosRepository kudosRepository;
    private SecurityServiceInterface securityService;
    @PersistenceContext
    private EntityManager em;

    @Override
    public ProofPagePagination proofsPagination(int page, int size, boolean sort) {
        var pageRequest = repository.findByStatus(
                Status.PUBLISHED,
                PageRequest.of(page, size, doSort(sort, DATA_CREATED)));
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toProofPagePagination(pageRequest);
    }

    @Override
    public ProofEntity addProofProfile(long talentId, ProofAddRequest proofAddRequest) {
        return repository.save(ProofEntity.builder()
                .title(proofAddRequest.title())
                .description(proofAddRequest.description())
                .link(proofAddRequest.link())
                .status(Status.DRAFT)
                .dateCreated(Instant.now())
                .user(userRepository.findById(talentId)
                        .orElseThrow(() -> new TalentNotFoundException(talentId)))
                .build());
    }

    @Override
    public ResponseEntity<?> getLocation(long talentId,
                                         ProofAddRequest proofAddRequest,
                                         Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        long proofId = addProofProfile(talentId, proofAddRequest).getProofId();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{proof-id}")
                .buildAndExpand(proofId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Override
    public ProofFullInfo proofUpdateRequest(long talentId, long id, ProofUpdateRequest proofUpdateRequest, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        if (!repository.existsByUser_UserIdAndProofId(talentId, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t have that talent");
        }
        var proofEntity = repository.findById(id)
                .orElseThrow(() -> new ProofNotFoundException(id));
        if (!proofEntity.getStatus().equals(Status.DRAFT)
                && proofUpdateRequest.status().equals(Status.DRAFT)) {
            throw new UserCanNotEditProofNotInDraftException();
        }
        if (proofEntity.getStatus().equals(Status.DRAFT)) {
            return changeStatusFromDraft(proofUpdateRequest, proofEntity);
        }
        if (proofUpdateRequest.status().equals(Status.HIDDEN)
                || proofUpdateRequest.status().equals(Status.PUBLISHED)) {
            proofEntity.setStatus(proofUpdateRequest.status());
        }
        proofEntity.setDateLastUpdated(Instant.now());
        repository.save(proofEntity);
        return mapper.toProofFullInfo(proofEntity);
    }

    private ProofFullInfo changeStatusFromDraft(ProofUpdateRequest proofUpdateRequest, ProofEntity proofEntity) {
        proofEntity.setTitle(validationField(
                proofUpdateRequest.title(),
                proofEntity.getTitle()));
        proofEntity.setDescription(validationField(
                proofUpdateRequest.description(),
                proofEntity.getDescription()));
        proofEntity.setLink(validationField(
                proofUpdateRequest.link(),
                proofEntity.getLink()));
        proofEntity.setStatus(proofUpdateRequest.status());
        proofEntity.setDateLastUpdated(Instant.now());
        repository.save(proofEntity);
        return mapper.toProofFullInfo(proofEntity);
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    @Override
    public void deleteProof(long talentId, long proofId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot delete proof another talent");
        }
        ProofEntity proof = repository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        proof.setUser(null);
        proof.setSkills(null);
        for (KudosEntity kudos : kudosRepository.findByProof_ProofId(proofId)) {
            kudos.setProof(null);
            kudos.setOwner(null);
            kudosRepository.deleteById(kudos.getKudosId());
        }
        proof.getKudos().clear();
        repository.deleteById(proofId);
    }

    @Override
    public ProofPagePagination getTalentAllProofs(Authentication auth, long talentId,
                                                  int page, int size, boolean sort, String status) {
        if (securityService.checkingLoggedAndToken(talentId, auth)) {
            Page<ProofEntity> pageRequest = getPaginationForTheTalent(talentId, page, size, sort, status);
            return mapper.toProofPagePaginationWithProofFullInfo(pageRequest);
        }
        var pageRequest = getPaginationForTheTalent(talentId, page,
                size, sort, Status.PUBLISHED.name());
        return mapper.toProofPagePaginationWithProofFullInfo(pageRequest);
    }

    private Page<ProofEntity> getPaginationForTheTalent(long talentId, int page, int size,
                                                        boolean sort, String status) {
        return (status.equals(Status.ALL.getStatus())) ?
                repository.findByUser_UserId(talentId,
                        PageRequest.of(page, size, doSort(sort, DATA_CREATED)))
                :
                repository.findByUser_UserIdAndStatus(talentId, Status.valueOf(status),
                        PageRequest.of(page, size, doSort(sort, DATA_CREATED)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProofPagePagination getTalentAllProofsWithKudoses(Authentication auth, long talentId,
                                                             int page, int size, boolean sort, String status) {
        if (securityService.checkingLoggedAndToken(talentId, auth)) {
            Page<ProofEntity> pageRequest = getPaginationForTheTalent(talentId, page, size, sort, status);
            return mapper.toProofPagePaginationWithProofFullInfoWithKudoses(pageRequest);
        }
        var pageRequest = getPaginationForTheTalent(talentId, page,
                size, sort, Status.PUBLISHED.name());

        return mapper.toProofPagePaginationWithProofFullInfoWithKudoses(pageRequest);
    }

    @Override
    public ProofFullInfo getProofFullInfo(Authentication auth, long proofId) {
        if (!repository.existsByProofId(proofId)) {
            throw new ProofNotFoundException(proofId);
        }
        ProofEntity proof = em.find(ProofEntity.class, proofId);
        var talentId = proof.getUser().getUserId();
        if (securityService.checkingLoggedAndToken(talentId, auth)) {
            var optionalProof = repository.findById(proofId);
            ProofEntity requestProof = optionalProof.orElseThrow(() -> new ProofNotFoundException(proofId));
            return mapper.toProofFullInfo(requestProof);
        }
        var optionalProof = repository.findByProofIdAndStatus(proofId, Status.PUBLISHED);
        ProofEntity requestProof = optionalProof.orElseThrow(() -> new ProofNotFoundException(proofId));
        return mapper.toProofFullInfo(requestProof);
    }

    public Sort doSort(boolean sort, String sortParam) {
        Sort dateSort;
        if (sort) {
            dateSort = Sort.by(sortParam).descending();
        } else {
            dateSort = Sort.by(sortParam);
        }
        return dateSort;
    }
}

