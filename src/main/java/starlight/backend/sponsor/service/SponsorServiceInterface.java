package starlight.backend.sponsor.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import starlight.backend.sponsor.model.request.SponsorUpdateRequest;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.response.SponsorKudosInfo;

public interface SponsorServiceInterface {
    SponsorKudosInfo getUnusableKudos(long sponsorId, Authentication auth);

    SponsorFullInfo getSponsorFullInfo(long sponsorId, Authentication auth);
    SponsorFullInfo updateSponsorProfile(long id, SponsorUpdateRequest sponsorUpdateRequest, Authentication auth);

    void deleteSponsor(long sponsorId, Authentication authentication);

    String getSponsorMail(long sponsorId, Authentication auth);

    ResponseEntity<String> sendEmailForRecoverySponsorAccount(long sponsorId, Authentication auth);
}
