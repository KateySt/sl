package starlight.backend.exception.user.sponsor;

public class SponsorCanNotSeeAnotherSponsor extends RuntimeException{
    public SponsorCanNotSeeAnotherSponsor() {
        super("Sponsor can't see another sponsor");
    }
}
