package starlight.backend.exception.user.talent;

public class TalentNotFoundException extends RuntimeException{
    public TalentNotFoundException(long id) {
        super("Talent not found by id " + id);
    }
}
