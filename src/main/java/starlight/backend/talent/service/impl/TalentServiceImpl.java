package starlight.backend.talent.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.talent.MapperTalent;
import starlight.backend.talent.model.request.TalentUpdateRequest;
import starlight.backend.talent.model.response.TalentFullInfo;
import starlight.backend.talent.model.response.TalentPagePagination;
import starlight.backend.talent.service.TalentServiceInterface;
import starlight.backend.user.model.entity.PositionEntity;
import starlight.backend.user.model.entity.UserEntity;
import starlight.backend.user.repository.PositionRepository;
import starlight.backend.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class TalentServiceImpl implements TalentServiceInterface {
    private MapperTalent mapper;
    private UserRepository userRepository;
    private PositionRepository positionRepository;
    private SecurityServiceInterface securityService;
    private ProofRepository proofRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public TalentPagePagination talentPagination(int page, int size) {
        var pageRequest = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("userId").descending())
        );
        if (page >= pageRequest.getTotalPages())
            throw new PageNotFoundException(page);
        return mapper.toTalentPagePagination(pageRequest);
    }

    @Override
    public TalentFullInfo talentFullInfo(long id) {
        return userRepository.findById(id)
                .map(mapper::toTalentFullInfo)
                .orElseThrow(() -> new TalentNotFoundException(id));
    }

    @Override
    public TalentFullInfo updateTalentProfile(long id, TalentUpdateRequest talentUpdateRequest, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(id, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot change another talent");
        }
        return userRepository.findById(id).map(talent -> {
            talent.setFullName(validationField(
                    talentUpdateRequest.fullName(),
                    talent.getFullName()));
            talent.setBirthday(talentUpdateRequest.avatar() == null ?
                    talent.getBirthday() :
                    talentUpdateRequest.birthday());
            talent.setPassword(
                    talentUpdateRequest.password() == null ?
                            talent.getPassword() :
                            passwordEncoder.encode(talentUpdateRequest.password())
            );
            talent.setAvatar(validationField(
                    talentUpdateRequest.avatar(),
                    talent.getAvatar()));
            talent.setEducation(validationField(
                    talentUpdateRequest.education(),
                    talent.getEducation()));
            talent.setExperience(validationField(
                    talentUpdateRequest.experience(),
                    talent.getExperience()));
            talent.setPositions(validationPosition(
                    talent.getPositions(),
                    talentUpdateRequest.positions()));
            userRepository.save(talent);
            return mapper.toTalentFullInfo(talent);
        }).orElseThrow(() -> new TalentNotFoundException(id));
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    private Set<PositionEntity> validationPosition(Set<PositionEntity> talentPositions,
                                                   List<String> positions) {
        if (positions != null && !positions.isEmpty()) {
            Set<PositionEntity> newPosition = positions.stream()
                    .map(position -> {
                        if (position != null && !position.isEmpty()) {
                            PositionEntity pos;
                            if (positionRepository.existsByPositionIgnoreCase(position)) {
                                pos = positionRepository.findByPosition(position);
                            } else {
                                pos = new PositionEntity(position);
                            }
                            return pos;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            return !newPosition.isEmpty() ? newPosition : talentPositions;
        }
        return talentPositions;
    }

    @Override
    public void deleteTalentProfile(long talentId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot delete another talent");
        }
        UserEntity user = userRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(talentId));
        user.setPositions(null);
        user.getAuthorities().clear();
        if (!user.getProofs().isEmpty()) {
            for (ProofEntity proof : proofRepository.findByUser_UserId(talentId)) {
                proof.setKudos(null);
                proof.setUser(null);
                proofRepository.deleteById(proof.getProofId());
            }
        }
        user.getProofs().clear();
        userRepository.deleteById(user.getUserId());
    }
}
