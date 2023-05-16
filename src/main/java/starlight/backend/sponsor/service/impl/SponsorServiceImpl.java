package starlight.backend.sponsor.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.advice.config.AdviceConfiguration;
import starlight.backend.advice.model.entity.DelayedDeleteEntity;
import starlight.backend.advice.model.enums.DeletingEntityType;
import starlight.backend.advice.repository.DelayDeleteRepository;
import starlight.backend.advice.service.AdviceService;
import starlight.backend.email.service.EmailService;
import starlight.backend.exception.YouAreInDeletingProcess;
import starlight.backend.exception.user.sponsor.SponsorAlreadyOnDeleteList;
import starlight.backend.exception.user.sponsor.SponsorCanNotSeeAnotherSponsor;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.SponsorMapper;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.enums.SponsorStatus;
import starlight.backend.sponsor.model.request.SponsorUpdateRequest;
import starlight.backend.sponsor.model.response.KudosWithProofId;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.response.SponsorKudosInfo;
import starlight.backend.sponsor.service.SponsorServiceInterface;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class SponsorServiceImpl implements SponsorServiceInterface {
    private SponsorRepository sponsorRepository;
    private SecurityServiceInterface securityService;
    private DelayDeleteRepository delayDeleteRepository;
    private AdviceConfiguration adviceConfiguration;
    private SecurityServiceInterface serviceService;
    private SponsorMapper sponsorMapper;
    private EmailService emailService;
    private AdviceService adviceService;


    @Override
    public SponsorKudosInfo getUnusableKudos(long sponsorId, Authentication auth) {
        isItMyAccount(sponsorId, auth);
        int alreadyMarkedKudos;
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        List<KudosWithProofId> kudosList = sponsor.getKudos()
                .stream()
                .map(el -> sponsorMapper.toKudosWithProofId(el))
                .toList();
        if (kudosList.isEmpty()) {
            alreadyMarkedKudos = 0;
        } else {
            alreadyMarkedKudos = kudosList.stream()
                    .map(KudosWithProofId::countKudos)
                    .reduce(Integer::sum)
                    .get();
        }
        return new SponsorKudosInfo(sponsor.getUnusedKudos(), alreadyMarkedKudos, kudosList);
    }

    @Override
    public SponsorFullInfo getSponsorFullInfo(long sponsorId, Authentication auth) {
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        isItMyAccount(sponsorId, auth);
        return SponsorFullInfo.builder()
                .fullName(sponsor.getFullName())
                .avatar(sponsor.getAvatar())
                .company(sponsor.getCompany())
                .unusedKudos(sponsor.getUnusedKudos())
                .build();
    }

    private void isItMyAccount(long sponsorId, Authentication auth) {
        if (!serviceService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new SponsorCanNotSeeAnotherSponsor();
        }
    }

    @Override
    public SponsorFullInfo updateSponsorProfile(long sponsorId, SponsorUpdateRequest sponsorUpdateRequest, Authentication auth) {
        if (!securityService.isSponsorActive(auth)) {
            throw new YouAreInDeletingProcess();
        }
        if (!serviceService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot change another sponsor");
        }
        return sponsorRepository.findById(sponsorId).map(sponsor -> {
                    sponsor.setAvatar(validationField(
                            sponsorUpdateRequest.avatar(),
                            sponsor.getAvatar()));
                    sponsor.setCompany(validationField(
                            sponsorUpdateRequest.company(),
                            sponsor.getCompany()));
                    sponsor.setFullName(validationField(
                            sponsorUpdateRequest.fullName(),
                            sponsor.getFullName()));
                    sponsor.setUnusedKudos(addKudos(sponsor, sponsorUpdateRequest.unusedKudos()));
                    sponsorRepository.save(sponsor);
                    return sponsorMapper.toSponsorFullInfo(sponsor);
                })
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
    }

    private int addKudos(SponsorEntity sponsor, int unusedKudos) {
        if (unusedKudos <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can't reduce the number of kudos");
        }
        var countKudos = unusedKudos + sponsor.getUnusedKudos();
        if(countKudos>=100000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can't add more 100 000 kudos");
        }
        sponsor.setUnusedKudos(countKudos);
        return sponsor.getUnusedKudos();
    }

    private String validationField(String newParam, String lastParam) {
        return newParam == null ?
                lastParam :
                newParam;
    }

    @Override
    @Transactional
    public void deleteSponsor(long sponsorId, Authentication auth) {
        if (!sponsorRepository.existsBySponsorId(sponsorId)) {
            throw new SponsorNotFoundException(sponsorId);
        }
        if (!securityService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot delete this sponsor");
        }

        sponsorRepository.findById(sponsorId).ifPresent(sponsor -> {
            if (delayDeleteRepository.existsByEntityId(sponsor.getSponsorId())) {
                throw new SponsorAlreadyOnDeleteList(sponsor.getSponsorId());
            }
            delayDeleteRepository.save(
                    DelayedDeleteEntity.builder()
                            .entityId(sponsor.getSponsorId())
                            .deletingEntityType(DeletingEntityType.SPONSOR)
                            .deleteDate(Instant.now().plus(adviceConfiguration.delayDays(), ChronoUnit.DAYS))
                            .userDeletingProcessUuid(UUID.randomUUID())
                            .build()
            );
            sponsor.setStatus(SponsorStatus.DELETING);
            sponsorRepository.save(sponsor);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public String getSponsorMail(long sponsorId, Authentication auth) {
        if (!serviceService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot get another sponsor");
        }
        if (!sponsorRepository.existsBySponsorId(sponsorId)) {
            throw new SponsorNotFoundException(sponsorId);
        }
        return sponsorRepository.findById(sponsorId)
                .map(SponsorEntity::getEmail)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<String> sendEmailForRecoverySponsorAccount(long sponsorId, Authentication auth) {
        String email = getSponsorMail(sponsorId, auth);
        emailService.sendRecoveryMessageSponsorAccount(email, adviceService.getUUID(sponsorId));
        log.info("Email sent to sponsor {}", email);
        return ResponseEntity.ok("Email sent to " + email);

    }
}
